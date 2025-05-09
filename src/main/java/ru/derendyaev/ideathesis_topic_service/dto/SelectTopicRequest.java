package ru.derendyaev.ideathesis_topic_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class SelectTopicRequest {
    @NotNull(message = "ID темы не может быть пустым")
    private Long topicId;
}
