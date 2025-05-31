package ru.derendyaev.ideathesis_topic_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.derendyaev.ideathesis_topic_service.dto.*;
import ru.derendyaev.ideathesis_topic_service.dto.changelog.TopicChangeLogDto;
import ru.derendyaev.ideathesis_topic_service.dto.comment.AddCommentRequest;
import ru.derendyaev.ideathesis_topic_service.dto.comment.TopicCommentDto;
import ru.derendyaev.ideathesis_topic_service.dto.update.PendingTopicSelectionDto;
import ru.derendyaev.ideathesis_topic_service.dto.update.UpdateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.model.TopicStatus;
import ru.derendyaev.ideathesis_topic_service.service.TopicChangeLogService;
import ru.derendyaev.ideathesis_topic_service.service.TopicCommentService;
import ru.derendyaev.ideathesis_topic_service.service.TopicGenerationService;
import ru.derendyaev.ideathesis_topic_service.service.TopicManagementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class TopicController {

    private final TopicGenerationService topicGenerationService;
    private final TopicManagementService topicManagementService;
    private final TopicChangeLogService topicChangeLogService;
    private final TopicCommentService topicCommentService;

    @PostMapping("/generate")
    public ResponseEntity<GenerateTopicResponse> generateTopics(
            @RequestHeader("X-Student-Guid") String studentGuid,
            @RequestBody @Valid GenerateTopicRequest request) {
        UUID guid = UUID.fromString(studentGuid);
        GenerateTopicResponse response = topicGenerationService.generateAndSaveTopics(guid, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/select")
    public ResponseEntity<Void> selectTopic(
            @RequestHeader("X-Student-Guid") String studentGuid,
            @RequestBody @Valid SelectTopicRequest request) {
        UUID guid = UUID.fromString(studentGuid);
        topicManagementService.selectTopic(guid, request.getTopicId(), UUID.fromString(request.getSupervisorGuid()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/status")
    public ResponseEntity<String> updateTopicStatus(
            @RequestHeader("X-Student-Guid") String studentGuid,
            @RequestBody @Valid TopicStatusUpdateRequest request) {
        UUID guid = UUID.fromString(studentGuid);
        topicManagementService.updateTopicStatus(request.getTopicId(), request, guid);
        return ResponseEntity.ok("Статус темы успешно обновлен");
    }

    @GetMapping("/history")
    public ResponseEntity<PaginatedResponse<GeneratedTopicDto>> getLastTenTopics(
            @RequestHeader("X-Student-Guid") String studentGuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID guid = UUID.fromString(studentGuid);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GeneratedTopicDto> topicPage = topicManagementService.getLastTenTopics(guid, pageable);
        PaginatedResponse<GeneratedTopicDto> response = new PaginatedResponse<>();
        response.setContent(topicPage.getContent());
        response.setPage(topicPage.getNumber());
        response.setSize(topicPage.getSize());
        response.setTotalElements(topicPage.getTotalElements());
        response.setTotalPages(topicPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/legacy")
    public ResponseEntity<GenerateTopicResponse> getLastTenTopicsLegacy(
            @RequestHeader("X-Student-Guid") String studentGuid) {
        UUID guid = UUID.fromString(studentGuid);
        GenerateTopicResponse response = topicManagementService.getLastTenTopics(guid);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{topicId}/history")
    public ResponseEntity<List<TopicChangeLogDto>> getTopicHistory(
            @PathVariable Long topicId) {
        List<TopicChangeLogDto> history = topicChangeLogService.getTopicHistory(topicId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/students/{studentGuid}/topics/active")
    public ResponseEntity<List<StudentTopicSelectionDto>> getActiveTopicsForStudent(
            @PathVariable String studentGuid,
            @RequestParam(required = false) TopicStatus status) {
        UUID guid = UUID.fromString(studentGuid);
        List<StudentTopicSelectionDto> activeTopics = topicManagementService.getActiveTopicsForStudent(guid, status);
        return ResponseEntity.ok(activeTopics);
    }

    @PostMapping("/topics/{topicId}/withdraw")
    public ResponseEntity<Void> withdrawTopic(
            @PathVariable Long topicId,
            @RequestHeader("X-Student-Guid") String studentGuid) {
        UUID guid = UUID.fromString(studentGuid);
        topicManagementService.withdrawTopic(topicId, guid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{topicId}/comments")
    public ResponseEntity<TopicCommentDto> addComment(
            @PathVariable Long topicId,
            @RequestHeader(value = "X-Student-Guid", required = false) String studentGuid,
            @RequestHeader(value = "X-Teacher-Guid", required = false) String teacherGuid,
            @RequestBody @Valid AddCommentRequest request) {
        UUID authorGuid;
        String authorType;
        if (studentGuid != null) {
            authorGuid = UUID.fromString(studentGuid);
            authorType = "STUDENT";
        } else if (teacherGuid != null) {
            authorGuid = UUID.fromString(teacherGuid);
            authorType = "TEACHER";
        } else {
            throw new IllegalArgumentException("Должен быть указан либо X-Student-Guid, либо X-Teacher-Guid");
        }

        TopicCommentDto commentDto = topicCommentService.addComment(topicId, authorType, authorGuid, request.getCommentText(), request.getParentCommentId());
        return ResponseEntity.ok(commentDto);
    }

    @GetMapping("/{topicId}/comments")
    public ResponseEntity<List<TopicCommentDto>> getCommentsForTopic(@PathVariable Long topicId) {
        List<TopicCommentDto> comments = topicCommentService.getCommentsForTopic(topicId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<GeneratedTopicDto> getTopic(@PathVariable Long topicId) {
        GeneratedTopicDto topic = topicManagementService.getTopic(topicId);
        return ResponseEntity.ok(topic);
    }

    @PutMapping("/{topicId}")
    public ResponseEntity<Void> updateTopic(
            @PathVariable Long topicId,
            @RequestHeader("X-Student-Guid") String studentGuid,
            @RequestBody @Valid UpdateTopicRequest request) {
        UUID guid = UUID.fromString(studentGuid);
        topicManagementService.updateTopic(topicId, guid, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/students/{studentGuid}/topics/generated")
    public ResponseEntity<PaginatedResponse<GeneratedTopicDto>> getGeneratedTopicsForStudent(
            @PathVariable String studentGuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID guid = UUID.fromString(studentGuid);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GeneratedTopicDto> topics = topicManagementService.getGeneratedTopics(guid, pageable);
        PaginatedResponse<GeneratedTopicDto> response = new PaginatedResponse<>();
        response.setContent(topics.getContent());
        response.setPage(topics.getNumber());
        response.setSize(topics.getSize());
        response.setTotalElements(topics.getTotalElements());
        response.setTotalPages(topics.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teachers/{teacherGuid}/topics/pending")
    public ResponseEntity<List<PendingTopicSelectionDto>> getPendingTopicsForTeacher(
            @PathVariable String teacherGuid) {
        UUID guid = UUID.fromString(teacherGuid);
        List<PendingTopicSelectionDto> pendingTopics = topicManagementService.getPendingTopicsForTeacher(guid);
        return ResponseEntity.ok(pendingTopics);
    }
}