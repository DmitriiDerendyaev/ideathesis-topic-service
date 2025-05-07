package ru.derendyaev.ideathesis_topic_service.service;

import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicResponse;
import ru.derendyaev.ideathesis_topic_service.dto.GeneratedTopicDto;
import ru.derendyaev.ideathesis_topic_service.exceptions.GigaChatException;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.GigaMessageResponse;
import ru.derendyaev.ideathesis_topic_service.gigaChat.role.RolePromptAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.derendyaev.ideathesis_topic_service.restUtils.Client;


import java.util.ArrayList;
import java.util.List;

/**
 * Сервис генерирует темы, обращаясь к GigaChat через GigaChatClient.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TopicGenerationService {

    private final Client gigaChatClient;
    private final RolePromptAction rolePromptAction;

    /**
     * Основной метод генерации тем.
     *
     * @param request объект с компетенциями, областью и уровнем обучения
     * @return ответ со списком тем
     */
    public GenerateTopicResponse generateTopics(GenerateTopicRequest request) {
        // 1) Формируем "system" и "user" промпты
        String systemPrompt = rolePromptAction.getBachelorRolePrompt(); // По умолчанию
        if ("MASTER".equalsIgnoreCase(request.getEducationLevel())) {
            systemPrompt = rolePromptAction.getMasterRolePrompt();
        }

        // Можно добавлять контекст: "Ты выступаешь как эксперт..."
        String finalSystemPrompt = "Ты отвечаешь как эксперт..." + "\n\n" + systemPrompt;

        // User Prompt — это компетенции и область
        String userPrompt = String.format("Компетенции: %s\nОбласть исследования: %s",
                request.getCompetencies(), request.getAreaOfStudy());

        // 2) Делаем запрос к GigaChat
        GigaMessageResponse response;
        try {
            response = gigaChatClient.gigaMessageGenerate(finalSystemPrompt, userPrompt, null);
        } catch (Exception e) {
            log.error("Ошибка при вызове GigaChat: ", e);
            throw new GigaChatException("Не удалось получить ответ от GigaChat", e);
        }

        // 3) Парсим полученный текст
        String rawAnswer = response.toString(); // или response.getChoices().get(0).getMessage().getContent()
        // Простейший способ — парсить текст, разбивать на 3 темы.
        // Здесь для примера - "заглушка" обработки.
        List<GeneratedTopicDto> dtos = parseTopicsFromGigaChat(rawAnswer);

        // 4) Формируем ответ
        GenerateTopicResponse result = new GenerateTopicResponse();
        result.setTopics(dtos);
        return result;
    }

    /**
     * Условный парсинг ответа. В реальном проекте нужен более надёжный разбор:
     * - Регулярные выражения
     * - JSON-формат
     * - markdown
     */
    private List<GeneratedTopicDto> parseTopicsFromGigaChat(String rawText) {
        List<GeneratedTopicDto> result = new ArrayList<>();
        // Заглушка: создаём одну тему из всего текста
        GeneratedTopicDto dto = new GeneratedTopicDto();
        dto.setTitle("Сгенерированная тема #1");
        dto.setDescription(rawText);
        dto.setRecommendedSkills(new String[] {"Java", "Spring", "React"});
        result.add(dto);
        return result;
    }
}
