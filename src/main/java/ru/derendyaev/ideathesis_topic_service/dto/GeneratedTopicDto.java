package ru.derendyaev.ideathesis_topic_service.dto;

import lombok.Data;

@Data
public class GeneratedTopicDto {
    private Long id; // Добавляем ID темы
    private String title;
    private String description;
    private String actuality;
    private String problems;
    private String[] recommendedSkills;
}