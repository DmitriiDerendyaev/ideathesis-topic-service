package ru.derendyaev.ideathesis_topic_service.dto;

import lombok.Data;

@Data
public class GeneratedTopicDto {
    private String title;
    private String description;
    private String actuality;
    private String problems;
    private String[] recommendedSkills;
}