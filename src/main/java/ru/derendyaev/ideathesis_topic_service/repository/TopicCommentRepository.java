package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.TopicComment;

import java.util.List;

public interface TopicCommentRepository extends JpaRepository<TopicComment, Long> {
    List<TopicComment> findByTopicIdOrderByCreatedAtAsc(Long topicId);
}
