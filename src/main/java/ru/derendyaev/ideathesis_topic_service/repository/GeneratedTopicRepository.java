package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.GeneratedTopic;

public interface GeneratedTopicRepository extends JpaRepository<GeneratedTopic, Long> {
}
