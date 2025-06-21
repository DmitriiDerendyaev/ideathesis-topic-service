package ru.derendyaev.ideathesis_topic_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.derendyaev.ideathesis_topic_service.dto.comment.TopicCommentDto;
import ru.derendyaev.ideathesis_topic_service.model.GeneratedTopic;
import ru.derendyaev.ideathesis_topic_service.model.TopicComment;
import ru.derendyaev.ideathesis_topic_service.model.TopicSelection;
import ru.derendyaev.ideathesis_topic_service.model.TopicStatus;
import ru.derendyaev.ideathesis_topic_service.repository.GeneratedTopicRepository;
import ru.derendyaev.ideathesis_topic_service.repository.TopicCommentRepository;
import ru.derendyaev.ideathesis_topic_service.repository.TopicSelectionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicCommentService {

    private final TopicCommentRepository topicCommentRepository;
    private final GeneratedTopicRepository generatedTopicRepository;
    private final TopicSelectionRepository topicSelectionRepository;

    @Transactional
    public TopicCommentDto addComment(Long topicId, String authorType, UUID authorGuid, String commentText, Long parentCommentId) {
        GeneratedTopic topic = generatedTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Топик не найден"));

//        // Проверка статуса топика
//        if (!topic.getStatus().equals(TopicStatus.PENDING) && !topic.getStatus().equals(TopicStatus.NEEDS_REVISION)) {
//            throw new IllegalStateException("Комментарии разрешены только для топиков со статусом PENDING или NEEDS_REVISION");
//        }

        // Проверка прав доступа
        TopicSelection selection = topicSelectionRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalStateException("Не найдена запись о выборе топика"));

        if ("STUDENT".equals(authorType) && !selection.getStudentGuid().equals(authorGuid)) {
            throw new IllegalStateException("Только студент, выбравший этот топик, может оставлять комментарии");
        } else if ("TEACHER".equals(authorType) && !selection.getSupervisorGuid().equals(authorGuid)) {
            throw new IllegalStateException("Только супервизор этого топика может оставлять комментарии");
        }

        // Проверка родительского комментария
        TopicComment parentComment = null;
        if (parentCommentId != null) {
            parentComment = topicCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new IllegalArgumentException("Родительский комментарий не найден"));
        }

        // Создание и сохранение комментария
        TopicComment comment = new TopicComment();
        comment.setTopic(topic);
        comment.setAuthorType(authorType);
        comment.setAuthorGuid(authorGuid);
        comment.setCommentText(commentText);
        comment.setParentComment(parentComment);

        return toDto(topicCommentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<TopicCommentDto> getCommentsForTopic(Long topicId) {
        return topicCommentRepository.findByTopicIdOrderByCreatedAtAsc(topicId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TopicCommentDto toDto(TopicComment comment) {
        TopicCommentDto dto = new TopicCommentDto();
        dto.setId(comment.getId());
        dto.setTopicId(comment.getTopic().getId());
        dto.setAuthorType(comment.getAuthorType());
        dto.setAuthorGuid(comment.getAuthorGuid());
        dto.setCommentText(comment.getCommentText());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        return dto;
    }
}
