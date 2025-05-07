package ru.derendyaev.ideathesis_topic_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicRequest;
import ru.derendyaev.ideathesis_topic_service.dto.GenerateTopicResponse;
import ru.derendyaev.ideathesis_topic_service.dto.GeneratedTopicDto;
import ru.derendyaev.ideathesis_topic_service.exceptions.GigaChatException;
import ru.derendyaev.ideathesis_topic_service.gigaChat.PromptConstants;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.GigaMessageRequest;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.GigaMessageResponse;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.Message;
import ru.derendyaev.ideathesis_topic_service.restUtils.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.derendyaev.ideathesis_topic_service.gigaChat.models.GigaChatConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicGenerationService {

    private final Client gigaChatClient;

    public GenerateTopicResponse generateTopics(GenerateTopicRequest request) {
        String systemPrompt = PromptConstants.SYSTEM_PROMPT;
        String userPrompt = "MASTER".equalsIgnoreCase(request.getEducationLevel())
                ? PromptConstants.getMasterPrompt(request.getCompetencies(), request.getAreaOfStudy())
                : PromptConstants.getBachelorPrompt(request.getCompetencies(), request.getAreaOfStudy());

        GigaMessageRequest gigaRequest = new GigaMessageRequest(
                GIGA_CHAT_MODEL,
                false, // stream
                0,     // updateInterval
                List.of(
                        new Message(SYSTEM_ROLE, systemPrompt),
                        new Message(USER_ROLE, userPrompt)
                ),
                1,     // n
                1024,   // max_tokens
                1.0    // repetition_penalty
        );

        GigaMessageResponse response;
        try {
            response = gigaChatClient.gigaMessageGenerate(gigaRequest);
        }
        catch (Exception e) {
            log.error("Ошибка при вызове GigaChat: ", e);
            throw new GigaChatException("Не удалось получить ответ от GigaChat", e);
        }

        String rawAnswer = response.getChoices().get(0).getMessage().getContent();
        List<GeneratedTopicDto> topics = parseTopicsFromGigaChat(rawAnswer);

        GenerateTopicResponse result = new GenerateTopicResponse();
        result.setTopics(topics);
        return result;
    }

    private List<GeneratedTopicDto> parseTopicsFromGigaChat(String rawText) {
        List<GeneratedTopicDto> topics = new ArrayList<>();
        // Шаблон для парсинга ответа
        Pattern pattern = Pattern.compile(
                "###\\s*Тема\\s*\\d+\\s*(?::\\s*(.*?))?\\n" +
                        "\\*\\*Описание\\*\\*:\\s*(.+?)\\n" +
                        "\\*\\*Актуальность\\*\\*:\\s*(.+?)\\n" +
                        "\\*\\*Проблемы\\*\\*:\\s*(.+?)\\n" +
                        "\\*\\*Стек технологий\\*\\*:\\s*(.+?)(?:\\n\\n|\\n?$)",
                Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(rawText);

        while (matcher.find()) {
            try {
                GeneratedTopicDto dto = new GeneratedTopicDto();
                // Если заголовок после двоеточия отсутствует, используем описание как заголовок
                String title = matcher.group(1) != null && !matcher.group(1).trim().isEmpty()
                        ? matcher.group(1).trim()
                        : matcher.group(2).trim();
                dto.setTitle(title);
                dto.setDescription(matcher.group(2).trim());
                dto.setActuality(matcher.group(3).trim());
                dto.setProblems(matcher.group(4).trim());
                // Очищаем стек технологий от лишних символов (например, точки в конце)
                String techStack = matcher.group(5).trim().replaceAll("[\\.\\s]+$", "");
                dto.setRecommendedSkills(techStack.isEmpty() ? new String[0] : techStack.split(",\\s*"));
                topics.add(dto);
            } catch (Exception e) {
                log.warn("Ошибка при парсинге темы: {}", matcher.group(0), e);
            }
        }

        // Если ничего не распарсено, возвращаем заглушку
        if (topics.isEmpty()) {
            log.warn("Не удалось распарсить темы из ответа: {}", rawText);
            GeneratedTopicDto fallback = new GeneratedTopicDto();
            fallback.setTitle("Сгенерированная тема");
            fallback.setDescription(rawText);
            fallback.setActuality("Не указана");
            fallback.setProblems("Не указаны");
            fallback.setRecommendedSkills(new String[]{"Неизвестно"});
            topics.add(fallback);
        }

        return topics;
    }
}