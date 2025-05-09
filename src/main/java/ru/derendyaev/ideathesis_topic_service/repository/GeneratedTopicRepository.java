package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.derendyaev.ideathesis_topic_service.model.GeneratedTopic;
import ru.derendyaev.ideathesis_topic_service.model.TopicStatus;

import java.util.List;
import java.util.UUID;

public interface GeneratedTopicRepository extends JpaRepository<GeneratedTopic, Long> {

    @Query("SELECT t FROM GeneratedTopic t WHERE t.request.studentGuid = :studentGuid AND t.status NOT IN :statuses")
    List<GeneratedTopic> findByRequestStudentGuidAndStatusNotIn(
            @Param("studentGuid") UUID studentGuid,
            @Param("statuses") List<TopicStatus> statuses
    );

    @Query("SELECT t FROM GeneratedTopic t WHERE t.request.studentGuid = :studentGuid ORDER BY t.createdAt DESC")
    Page<GeneratedTopic> findByRequestStudentGuidOrderByCreatedAtDesc(
            @Param("studentGuid") UUID studentGuid,
            Pageable pageable
    );
}
