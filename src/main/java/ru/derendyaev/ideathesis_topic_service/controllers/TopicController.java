package ru.derendyaev.ideathesis_topic_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicResponse;
import ru.derendyaev.ideathesis_topic_service.service.TopicGenerationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicGenerationService topicGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<GenerateTopicResponse> generateTopics(
            @RequestHeader("X-Student-Guid") String studentGuid,
            @RequestBody @Valid GenerateTopicRequest request) {
        UUID guid = UUID.fromString(studentGuid);
        GenerateTopicResponse response = topicGenerationService.generateAndSaveTopics(guid, request);
        return ResponseEntity.ok(response);
    }
}