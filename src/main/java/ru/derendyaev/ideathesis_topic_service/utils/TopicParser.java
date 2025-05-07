package ru.derendyaev.ideathesis_topic_service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.derendyaev.ideathesis_topic_service.dto.GeneratedTopicDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TopicParser {

    private static final Pattern TOPIC_PATTERN = Pattern.compile(
            "###\\s*Тема\\s*\\d+\\s*(?::\\s*(.*?))?\\n" +
                    "\\*\\*Описание\\*\\*:\\s*(.+?)\\n" +
                    "\\*\\*Актуальность\\*\\*:\\s*(.+?)\\n" +
                    "\\*\\*Проблемы\\*\\*:\\s*(.+?)\\n" +
                    "\\*\\*Стек технологий\\*\\*:\\s*(.+?)(?=(?:###\\s*Тема\\s*\\d+|$))",
            Pattern.DOTALL
    );

    public List<GeneratedTopicDto> parseTopics(String rawText) {
        List<GeneratedTopicDto> topics = new ArrayList<>();
        Matcher matcher = TOPIC_PATTERN.matcher(rawText);

        while (matcher.find()) {
            try {
                GeneratedTopicDto dto = new GeneratedTopicDto();
                String title = matcher.group(1) != null && !matcher.group(1).trim().isEmpty()
                        ? matcher.group(1).trim()
                        : matcher.group(2).trim();
                dto.setTitle(title);
                dto.setDescription(matcher.group(2).trim());
                dto.setActuality(matcher.group(3).trim());
                dto.setProblems(matcher.group(4).trim());
                dto.setRecommendedSkills(parseTechStack(matcher.group(5).trim()));
                topics.add(dto);
            } catch (Exception e) {
                log.warn("Ошибка при парсинге темы: {}", matcher.group(0), e);
            }
        }

        if (topics.isEmpty()) {
            log.warn("Не удалось распарсить темы из ответа: {}", rawText);
            topics.add(createFallbackTopic(rawText));
        }

        return topics;
    }

    private String[] parseTechStack(String techStackRaw) {
        String[] techStack;
        if (techStackRaw.contains("\n-")) {
            techStack = Arrays.stream(techStackRaw.split("\n-"))
                    .map(line -> line.replaceAll("^\\s*-\\s*", "")
                            .replaceAll("\\s*\\([^)]*\\)", "")
                            .replaceAll("[\\.\\s]+$", "")
                            .trim())
                    .filter(line -> !line.isEmpty())
                    .toArray(String[]::new);
        } else {
            techStack = techStackRaw.replaceAll("[\\.\\s]+$", "")
                    .replaceAll("\\s*\\([^)]*\\)", "")
                    .split(",\\s*");
        }
        return Arrays.stream(techStack)
                .filter(tech -> !tech.isEmpty())
                .toArray(String[]::new);
    }

    private GeneratedTopicDto createFallbackTopic(String rawText) {
        GeneratedTopicDto fallback = new GeneratedTopicDto();
        fallback.setTitle("Сгенерированная тема");
        fallback.setDescription(rawText);
        fallback.setActuality("Не указана");
        fallback.setProblems("Не указаны");
        fallback.setRecommendedSkills(new String[]{"Неизвестно"});
        return fallback;
    }
}