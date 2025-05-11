package ru.derendyaev.ideathesis_topic_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.derendyaev.ideathesis_topic_service.model.TopicStatus;

@Data
public class TopicStatusUpdateRequest {
    @NotNull(message = "ID темы не может быть пустым")
    private Long topicId;
    @NotNull(message = "Статус не может быть пустым")
    private TopicStatus status;
}
