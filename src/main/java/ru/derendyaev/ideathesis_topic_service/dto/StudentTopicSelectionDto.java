package ru.derendyaev.ideathesis_topic_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StudentTopicSelectionDto {
    private GeneratedTopicDto topic;
    private UUID supervisorGuid;
}
