package ru.derendyaev.ideathesis_topic_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class GenerateTopicResponse {
    private List<GeneratedTopicDto> topics;
}