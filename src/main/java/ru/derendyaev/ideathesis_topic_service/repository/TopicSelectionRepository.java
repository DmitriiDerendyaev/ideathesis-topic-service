package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.TopicSelection;

public interface TopicSelectionRepository extends JpaRepository<TopicSelection, Long> {
}
