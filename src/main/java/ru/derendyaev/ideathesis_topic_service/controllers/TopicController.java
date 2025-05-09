package ru.derendyaev.ideathesis_topic_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicResponse;
import ru.derendyaev.ideathesis_topic_service.dto.SelectTopicRequest;
import ru.derendyaev.ideathesis_topic_service.dto.TopicStatusUpdateRequest;
import ru.derendyaev.ideathesis_topic_service.model.GeneratedTopic;
import ru.derendyaev.ideathesis_topic_service.service.TopicGenerationService;
import ru.derendyaev.ideathesis_topic_service.service.TopicManagementService;
import ru.derendyaev.ideathesis_topic_service.service.TopicSelectionService;

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
        topicManagementService.selectTopic(guid, request.getTopicId());
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
    public ResponseEntity<List<GeneratedTopic>> getLastTenTopics(
            @RequestHeader("X-Student-Guid") String studentGuid) {
        UUID guid = UUID.fromString(studentGuid);
        return ResponseEntity.ok(topicManagementService.getLastTenTopics(guid));
    }
}