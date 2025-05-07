package ru.derendyaev.ideathesis_topic_service.exceptions;

public class GigaChatException extends RuntimeException {
    public GigaChatException(String message) {
        super(message);
    }

    public GigaChatException(String message, Throwable cause) {
        super(message, cause);
    }
}