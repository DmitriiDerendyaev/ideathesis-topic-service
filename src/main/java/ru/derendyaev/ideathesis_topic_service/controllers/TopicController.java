package ru.derendyaev.ideathesis_topic_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.derendyaev.ideathesis_topic_service.dto.*;
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
        topicManagementService.updateTopicStatus(request.getTopicId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<GenerateTopicResponse> getLastTenTopics(
            @RequestHeader("X-Student-Guid") String studentGuid) {
        UUID guid = UUID.fromString(studentGuid);
        GenerateTopicResponse response = topicManagementService.getLastTenTopics(guid);
        return ResponseEntity.ok(response);
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
}