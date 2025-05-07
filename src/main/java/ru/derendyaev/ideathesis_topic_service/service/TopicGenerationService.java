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
import java.util.Arrays;
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
        } catch (Exception e) {
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
        // Шаблон для парсинга, ограничивает стек технологий до следующей темы или конца текста
        Pattern pattern = Pattern.compile(
                "###\\s*Тема\\s*\\d+\\s*(?::\\s*(.*?))?\\n" +
                        "\\*\\*Описание\\*\\*:\\s*(.+?)\\n" +
                        "\\*\\*Актуальность\\*\\*:\\s*(.+?)\\n" +
                        "\\*\\*Проблемы\\*\\*:\\s*(.+?)\\n" +
                        "\\*\\*Стек технологий\\*\\*:\\s*(.+?)(?=(?:###\\s*Тема\\s*\\d+|$))",
                Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(rawText);

        while (matcher.find()) {
            try {
                GeneratedTopicDto dto = new GeneratedTopicDto();
                // Используем описание как заголовок, если заголовок отсутствует
                String title = matcher.group(1) != null && !matcher.group(1).trim().isEmpty()
                        ? matcher.group(1).trim()
                        : matcher.group(2).trim();
                dto.setTitle(title);
                dto.setDescription(matcher.group(2).trim());
                dto.setActuality(matcher.group(3).trim());
                dto.setProblems(matcher.group(4).trim());

                // Обработка стека технологий
                String techStackRaw = matcher.group(5).trim();
                String[] techStack;
                if (techStackRaw.contains("\n-")) {
                    // Формат с дефисами и переносом строки
                    techStack = Arrays.stream(techStackRaw.split("\n-"))
                            .map(line -> line.replaceAll("^\\s*-\\s*", "") // Удаляем дефис
                                    .replaceAll("\\s*\\([^)]*\\)", "") // Удаляем комментарии в скобках
                                    .replaceAll("[\\.\\s]+$", "") // Удаляем точки и пробелы в конце
                                    .trim())
                            .filter(line -> !line.isEmpty())
                            .toArray(String[]::new);
                } else {
                    // Формат с запятыми
                    techStack = techStackRaw.replaceAll("[\\.\\s]+$", "") // Удаляем точки и пробелы в конце
                            .replaceAll("\\s*\\([^)]*\\)", "") // Удаляем комментарии в скобках
                            .split(",\\s*");
                }
                // Фильтрация пустых технологий
                techStack = Arrays.stream(techStack)
                        .filter(tech -> !tech.isEmpty())
                        .toArray(String[]::new);
                dto.setRecommendedSkills(techStack);
                topics.add(dto);
            } catch (Exception e) {
                log.warn("Ошибка при парсинге темы: {}", matcher.group(0), e);
            }
        }

        // Заглушка, если ничего не распарсено
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