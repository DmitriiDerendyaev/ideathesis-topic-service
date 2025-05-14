package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.TopicSelection;
import ru.derendyaev.ideathesis_topic_service.model.TopicStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicSelectionRepository extends JpaRepository<TopicSelection, Long> {
    Optional<TopicSelection> findByStudentGuidAndTopicId(UUID studentGuid, Long topicId);
    List<TopicSelection> findByTopicIdAndStudentGuidNot(Long topicId, UUID studentGuid);
    List<TopicSelection> findByStudentGuid(UUID studentGuid);
    Optional<TopicSelection> findByTopicId(Long topicId);
}