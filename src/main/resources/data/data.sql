-- Вставка тестовых данных в таблицу competencies
INSERT INTO competencies (competency_name, description) VALUES
    ('Java', 'Объектно-ориентированное программирование на Java'),
    ('Python', 'Программирование на Python для анализа данных и машинного обучения'),
    ('Spring', 'Фреймворк для разработки веб-приложений на Java'),
    ('MySQL', 'Работа с реляционными базами данных MySQL'),
    ('Apache Kafka', 'Работа с распределенными системами сообщений'),
    ('Machine Learning', 'Разработка моделей машинного обучения'),
    ('JavaScript', 'Программирование на JavaScript для веб-разработки'),
    ('React', 'Фреймворк для разработки пользовательских интерфейсов');

-- Вставка тестовых данных в таблицу study_areas
INSERT INTO study_areas (area_name, description) VALUES
    ('Медицина', 'Область, связанная с медицинскими технологиями и здравоохранением'),
    ('Информационные технологии', 'Разработка программного обеспечения и IT-систем'),
    ('Биоинформатика', 'Применение IT в биологии и геномике');

-- Вставка тестовых данных в таблицу user_competencies (два студента)
INSERT INTO user_competencies (student_guid, competency_id, created_at) VALUES
    ('123e4567-e89b-12d3-a456-426614174000', (SELECT competency_id FROM competencies WHERE competency_name = 'Java'), CURRENT_TIMESTAMP),
    ('123e4567-e89b-12d3-a456-426614174000', (SELECT competency_id FROM competencies WHERE competency_name = 'Python'), CURRENT_TIMESTAMP),
    ('123e4567-e89b-12d3-a456-426614174000', (SELECT competency_id FROM competencies WHERE competency_name = 'MySQL'), CURRENT_TIMESTAMP),
    ('223e4567-e89b-12d3-a456-426614174001', (SELECT competency_id FROM competencies WHERE competency_name = 'Python'), CURRENT_TIMESTAMP),
    ('223e4567-e89b-12d3-a456-426614174001', (SELECT competency_id FROM competencies WHERE competency_name = 'Machine Learning'), CURRENT_TIMESTAMP),
    ('223e4567-e89b-12d3-a456-426614174001', (SELECT competency_id FROM competencies WHERE competency_name = 'JavaScript'), CURRENT_TIMESTAMP);

-- Вставка тестовых данных в таблицу user_study_areas
INSERT INTO user_study_areas (student_guid, area_id, created_at) VALUES
    ('123e4567-e89b-12d3-a456-426614174000', (SELECT area_id FROM study_areas WHERE area_name = 'Информационные технологии'), CURRENT_TIMESTAMP),
    ('123e4567-e89b-12d3-a456-426614174000', (SELECT area_id FROM study_areas WHERE area_name = 'Медицина'), CURRENT_TIMESTAMP),
    ('223e4567-e89b-12d3-a456-426614174001', (SELECT area_id FROM study_areas WHERE area_name = 'Биоинформатика'), CURRENT_TIMESTAMP);

-- Вставка тестовых данных в таблицу user_requests
INSERT INTO user_requests (student_guid, area_id, request_time, request_text) VALUES
    ('123e4567-e89b-12d3-a456-426614174000',
     (SELECT area_id FROM study_areas WHERE area_name = 'Медицина'),
     CURRENT_TIMESTAMP,
     'Сгенерировать темы для диплома в области медицины с использованием Java и Python'),
    ('123e4567-e89b-12d3-a456-426614174000',
     (SELECT area_id FROM study_areas WHERE area_name = 'Информационные технологии'),
     CURRENT_TIMESTAMP,
     'Сгенерировать темы для диплома в области IT с использованием Java и MySQL'),
    ('223e4567-e89b-12d3-a456-426614174001',
     (SELECT area_id FROM study_areas WHERE area_name = 'Биоинформатика'),
     CURRENT_TIMESTAMP,
     'Сгенерировать темы для диплома в области биоинформатики с использованием Python и Machine Learning');

-- Вставка тестовых данных в таблицу request_competencies
INSERT INTO request_competencies (request_id, competency_id, created_at) VALUES
    (1, (SELECT competency_id FROM competencies WHERE competency_name = 'Java'), CURRENT_TIMESTAMP),
    (1, (SELECT competency_id FROM competencies WHERE competency_name = 'Python'), CURRENT_TIMESTAMP),
    (2, (SELECT competency_id FROM competencies WHERE competency_name = 'Java'), CURRENT_TIMESTAMP),
    (2, (SELECT competency_id FROM competencies WHERE competency_name = 'MySQL'), CURRENT_TIMESTAMP),
    (3, (SELECT competency_id FROM competencies WHERE competency_name = 'Python'), CURRENT_TIMESTAMP),
    (3, (SELECT competency_id FROM competencies WHERE competency_name = 'Machine Learning'), CURRENT_TIMESTAMP);

-- Вставка тестовых данных в таблицу generated_topics
INSERT INTO generated_topics (request_id, title, description, actuality, problems, created_at) VALUES
    (1,
     'Разработка системы мониторинга состояния пациента с острым отитом',
     'Система для анализа лабораторных данных пациентов с острым отитом.',
     'Острый отит требует точного мониторинга для предотвращения осложнений.',
     'Отсутствие автоматизированных систем мониторинга, высокая нагрузка на персонал.',
     CURRENT_TIMESTAMP),
    (1,
     'Мобильное приложение для отслеживания симптомов хронических заболеваний',
     'Приложение для пациентов с хроническими заболеваниями.',
     'Растет потребность в цифровых решениях для здравоохранения.',
     'Недостаток персонализированных инструментов для пациентов.',
     CURRENT_TIMESTAMP),
    (2,
     'Разработка веб-приложения для управления проектами',
     'Веб-приложение для управления IT-проектами.',
     'Эффективное управление проектами критично для IT-компаний.',
     'Сложности с интеграцией данных из разных источников.',
     CURRENT_TIMESTAMP),
    (3,
     'Анализ геномных данных с помощью машинного обучения',
     'Система для анализа геномных данных.',
     'Биоинформатика требует новых методов анализа больших данных.',
     'Высокая вычислительная сложность анализа геномов.',
     CURRENT_TIMESTAMP);

-- Вставка тестовых данных в таблицу topic_skills
INSERT INTO topic_skills (topic_id, competency_id, created_at) VALUES
    (1, (SELECT competency_id FROM competencies WHERE competency_name = 'Java'), CURRENT_TIMESTAMP),
    (1, (SELECT competency_id FROM competencies WHERE competency_name = 'Python'), CURRENT_TIMESTAMP),
    (1, (SELECT competency_id FROM competencies WHERE competency_name = 'MySQL'), CURRENT_TIMESTAMP),
    (2, (SELECT competency_id FROM competencies WHERE competency_name = 'Java'), CURRENT_TIMESTAMP),
    (2, (SELECT competency_id FROM competencies WHERE competency_name = 'React'), CURRENT_TIMESTAMP),
    (3, (SELECT competency_id FROM competencies WHERE competency_name = 'Java'), CURRENT_TIMESTAMP),
    (3, (SELECT competency_id FROM competencies WHERE competency_name = 'Spring'), CURRENT_TIMESTAMP),
    (3, (SELECT competency_id FROM competencies WHERE competency_name = 'MySQL'), CURRENT_TIMESTAMP),
    (4, (SELECT competency_id FROM competencies WHERE competency_name = 'Python'), CURRENT_TIMESTAMP),
    (4, (SELECT competency_id FROM competencies WHERE competency_name = 'Machine Learning'), CURRENT_TIMESTAMP);

-- Вставка тестовых данных в таблицу topic_selections
INSERT INTO topic_selections (student_guid, topic_id, selected_at) VALUES
    ('123e4567-e89b-12d3-a456-426614174000', 1, CURRENT_TIMESTAMP),
    ('123e4567-e89b-12d3-a456-426614174000', 3, CURRENT_TIMESTAMP),
    ('223e4567-e89b-12d3-a456-426614174001', 4, CURRENT_TIMESTAMP);