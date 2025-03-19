package ru.derendyaev.mospolytech.gigaChat.models.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GigaMessageRequest {

    public GigaMessageRequest(String model, boolean stream, int updateInterval, List<Message> messages) {
        this.model = model;
        this.stream = stream;
        this.updateInterval = updateInterval;
        this.messages = messages;
    }

    @JsonProperty("model")
    private String model; // идентификатор модели

    @JsonProperty("stream")
    private boolean stream; // режим стриминга

    @JsonProperty("update_interval")
    private int updateInterval; // интервал обновления токенов

    @JsonProperty("messages")
    private List<Message> messages; // список сообщений
}
