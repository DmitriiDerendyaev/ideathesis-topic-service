package ru.derendyaev.ideathesis_topic_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class PaginatedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}