package controllers;

import dto.GenerateTopicRequest;
import dto.GenerateTopicResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.TopicGenerationService;

/**
 * REST-контроллер для управления темами.
 */
@RestController
@RequestMapping("/api/v1/topics")
@Validated
public class TopicController {

    private final TopicGenerationService topicGenerationService;

    public TopicController(TopicGenerationService topicGenerationService) {
        this.topicGenerationService = topicGenerationService;
    }

    /**
     * Генерация тем с помощью GigaChat.
     *
     * @param request запрос с компетенциями и т.д.
     * @return список сгенерированных тем
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerateTopicResponse> generateTopics(@RequestBody @Validated GenerateTopicRequest request) {
        GenerateTopicResponse response = topicGenerationService.generateTopics(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Пример эндпоинта для выбора темы студентом.
     * Здесь может быть логика сохранения темы, статуса, и т.д.
     */
    @PostMapping("/choose")
    public ResponseEntity<String> chooseTopic(@RequestParam Long topicId, @RequestParam Long userId) {
        // Заглушка: в реальном проекте можно проверить наличие темы в БД,
        // сохранить связь (topic -> user), установить статус "PENDING" и т.д.
        return ResponseEntity.ok("Тема " + topicId + " выбрана пользователем " + userId);
    }
}
