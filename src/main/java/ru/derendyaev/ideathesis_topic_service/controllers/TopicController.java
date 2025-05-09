package ru.derendyaev.ideathesis_topic_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicResponse;
import ru.derendyaev.ideathesis_topic_service.dto.SelectTopicRequest;
import ru.derendyaev.ideathesis_topic_service.service.TopicGenerationService;
import ru.derendyaev.ideathesis_topic_service.service.TopicSelectionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicGenerationService topicGenerationService;
    private final TopicSelectionService topicSelectionService;

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
        topicSelectionService.selectTopic(guid, request.getTopicId());
        return ResponseEntity.ok().build();
    }
}