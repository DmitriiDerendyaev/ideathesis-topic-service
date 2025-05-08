-- Удаление существующих таблиц (если они есть)
DROP TABLE IF EXISTS topic_selections;
DROP TABLE IF EXISTS topic_skills;
DROP TABLE IF EXISTS generated_topics;
DROP TABLE IF EXISTS request_competencies;
DROP TABLE IF EXISTS user_requests;
DROP TABLE IF EXISTS user_study_areas;
DROP TABLE IF EXISTS user_competencies;
DROP TABLE IF EXISTS study_areas;
DROP TABLE IF EXISTS competencies;

-- Создание таблиц с увеличенными ограничениями
CREATE TABLE competencies (
    competency_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    competency_name VARCHAR(2048) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE study_areas (
    area_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    area_name VARCHAR(2048) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE user_competencies (
    user_competency_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_guid UUID NOT NULL,
    competency_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_guid, competency_id),
    FOREIGN KEY (competency_id) REFERENCES competencies (competency_id)
);

CREATE TABLE user_study_areas (
    user_study_area_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_guid UUID NOT NULL,
    area_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_guid, area_id),
    FOREIGN KEY (area_id) REFERENCES study_areas (area_id)
);

CREATE TABLE user_requests (
    request_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_guid UUID NOT NULL,
    area_id BIGINT NOT NULL,
    request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    request_text TEXT NOT NULL,
    FOREIGN KEY (area_id) REFERENCES study_areas (area_id)
);

CREATE TABLE request_competencies (
    request_competency_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    request_id BIGINT NOT NULL,
    competency_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (request_id, competency_id),
    FOREIGN KEY (request_id) REFERENCES user_requests (request_id),
    FOREIGN KEY (competency_id) REFERENCES competencies (competency_id)
);

CREATE TABLE generated_topics (
    topic_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    request_id BIGINT NOT NULL,
    title VARCHAR(2048) NOT NULL,
    description TEXT NOT NULL,
    actuality TEXT NOT NULL,
    problems TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES user_requests (request_id)
);

CREATE TABLE topic_skills (
    topic_skill_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    competency_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (topic_id, competency_id),
    FOREIGN KEY (topic_id) REFERENCES generated_topics (topic_id),
    FOREIGN KEY (competency_id) REFERENCES competencies (competency_id)
);

CREATE TABLE topic_selections (
    selection_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_guid UUID NOT NULL,
    topic_id BIGINT NOT NULL,
    selected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_guid, topic_id),
    FOREIGN KEY (topic_id) REFERENCES generated_topics (topic_id)
);