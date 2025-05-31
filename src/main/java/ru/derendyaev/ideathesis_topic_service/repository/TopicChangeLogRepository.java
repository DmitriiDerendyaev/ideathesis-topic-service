package ru.derendyaev.ideathesis_topic_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.TopicChangeLog;

import java.util.List;

public interface TopicChangeLogRepository extends JpaRepository<TopicChangeLog, Long> {
    List<TopicChangeLog> findByTopicIdOrderByChangeTimeDesc(Long topicId);
}
