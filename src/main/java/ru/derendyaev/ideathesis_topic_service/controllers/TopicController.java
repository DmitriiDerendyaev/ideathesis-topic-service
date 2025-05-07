package ru.derendyaev.ideathesis_topic_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicResponse;
import ru.derendyaev.ideathesis_topic_service.service.TopicGenerationService;

/**
 * REST-контроллер для управления темами дипломных работ.
 */
@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {

    private final TopicGenerationService topicGenerationService;

    public TopicController(TopicGenerationService topicGenerationService) {
        this.topicGenerationService = topicGenerationService;
    }

    /**
     * Генерирует темы для дипломной работы на основе запроса.
     *
     * @param request запрос с компетенциями, областью обучения и уровнем
     * @return список сгенерированных тем
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerateTopicResponse> generateTopics(@RequestBody @Valid GenerateTopicRequest request) {
        GenerateTopicResponse response = topicGenerationService.generateTopics(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Выбор темы студентом (заглушка).
     * TODO: Реализовать сохранение темы в БД и проверку доступности.
     *
     * @param topicId идентификатор темы
     * @param userId  идентификатор пользователя
     * @return сообщение об успешном выборе
     */
    @PostMapping("/choose")
    public ResponseEntity<String> chooseTopic(@RequestParam Long topicId, @RequestParam Long userId) {
        return ResponseEntity.ok("Тема " + topicId + " выбрана пользователем " + userId);
    }
}