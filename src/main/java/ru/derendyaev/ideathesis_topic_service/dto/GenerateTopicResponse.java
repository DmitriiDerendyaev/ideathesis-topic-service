package ru.derendyaev.ideathesis_topic_service.dto;

import java.util.List;
import lombok.Data;

@Data
public class GenerateTopicResponse {
    private List<GeneratedTopicDto> topics;
}