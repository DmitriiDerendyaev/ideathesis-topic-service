package ru.derendyaev.ideathesis_topic_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.derendyaev.ideathesis_topic_service.dto.*;
import ru.derendyaev.ideathesis_topic_service.dto.changelog.TopicChangeLogDto;
import ru.derendyaev.ideathesis_topic_service.dto.comment.AddCommentRequest;
import ru.derendyaev.ideathesis_topic_service.dto.comment.TopicCommentDto;
import ru.derendyaev.ideathesis_topic_service.service.TopicChangeLogService;
import ru.derendyaev.ideathesis_topic_service.service.TopicCommentService;
import ru.derendyaev.ideathesis_topic_service.service.TopicGenerationService;
import ru.derendyaev.ideathesis_topic_service.service.TopicManagementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
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
    public ResponseEntity<Void> updateTopicStatus(
            @RequestHeader("X-Student-Guid") String studentGuid,
            @RequestBody @Valid TopicStatusUpdateRequest request) {
        UUID guid = UUID.fromString(studentGuid);
        topicManagementService.updateTopicStatus(request.getTopicId(), request, guid    );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<GenerateTopicResponse> getLastTenTopics(
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
    public ResponseEntity<List<StudentTopicSelectionDto>> getActiveTopicsForStudent(@PathVariable String studentGuid) {
        UUID guid = UUID.fromString(studentGuid);
        List<StudentTopicSelectionDto> activeTopics = topicManagementService.getActiveTopicsForStudent(guid);
        return ResponseEntity.ok(activeTopics);
    }

    @PostMapping("/topics/{topicId}/withdraw")
    public ResponseEntity<Void> withdrawTopic(@PathVariable Long topicId, @RequestHeader("X-Student-Guid") String studentGuid) {
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
}