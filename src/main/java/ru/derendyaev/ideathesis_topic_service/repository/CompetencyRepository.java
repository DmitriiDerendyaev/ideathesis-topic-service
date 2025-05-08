package ru.derendyaev.ideathesis_topic_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.derendyaev.ideathesis_topic_service.model.Competency;

import java.util.Optional;

public interface CompetencyRepository extends JpaRepository<Competency, Long> {
    Optional<Competency> findByCompetencyName(String competencyName);
}