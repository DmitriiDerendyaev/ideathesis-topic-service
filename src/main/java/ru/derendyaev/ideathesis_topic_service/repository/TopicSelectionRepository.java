package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.TopicSelection;

import java.util.List;
import java.util.UUID;

public interface TopicSelectionRepository extends JpaRepository<TopicSelection, Long> {
    List<TopicSelection> findByTopicIdAndStudentGuidNot(Long topicId, UUID studentGuid);
}