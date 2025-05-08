package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.StudyArea;

import java.util.Optional;

public interface StudyAreaRepository extends JpaRepository<StudyArea, Long> {
    Optional<StudyArea> findByAreaName(String areaName);
}
