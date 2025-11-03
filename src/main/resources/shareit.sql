-- =============================================
-- ShareIt Database Schema
-- Created for SPRINT_14 project
-- =============================================

-- Создание базы данных
DROP DATABASE IF EXISTS shareit;
CREATE DATABASE shareit;
USE shareit;

-- =============================================
-- Таблица пользователей
-- =============================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================
-- Таблица запросов на вещи
-- =============================================
CREATE TABLE requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT NOT NULL,
    requester_id BIGINT NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- Таблица вещей
-- =============================================
CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    owner_id BIGINT NOT NULL,
    request_id BIGINT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE SET NULL
);

-- =============================================
-- Таблица бронирований
-- =============================================
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status ENUM('WAITING', 'APPROVED', 'REJECTED', 'CANCELED') NOT NULL DEFAULT 'WAITING',
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- Таблица комментариев
-- =============================================
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text TEXT NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- Индексы для оптимизации производительности
-- =============================================

-- Индексы для пользователей
CREATE INDEX idx_users_email ON users(email);

-- Индексы для вещей
CREATE INDEX idx_items_owner_id ON items(owner_id);
CREATE INDEX idx_items_available ON items(is_available);
CREATE INDEX idx_items_name ON items(name);
CREATE INDEX idx_items_description ON items(description(255));
CREATE INDEX idx_items_request_id ON items(request_id);

-- Индексы для бронирований
CREATE INDEX idx_bookings_item_id ON bookings(item_id);
CREATE INDEX idx_bookings_booker_id ON bookings(booker_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_dates ON bookings(start_date, end_date);

-- Индексы для запросов
CREATE INDEX idx_requests_requester_id ON requests(requester_id);
CREATE INDEX idx_requests_created ON requests(created);

-- Индексы для комментариев
CREATE INDEX idx_comments_item_id ON comments(item_id);
CREATE INDEX idx_comments_author_id ON comments(author_id);
CREATE INDEX idx_comments_created ON comments(created);

-- =============================================
-- Вставка тестовых данных
-- =============================================

-- Тестовые пользователи
INSERT INTO users (name, email) VALUES
('Иван Иванов', 'ivan@example.com'),
('Петр Петров', 'petr@example.com'),
('Мария Сидорова', 'maria@example.com'),
('Алексей Козлов', 'alexey@example.com'),
('Екатерина Новикова', 'ekaterina@example.com'),
('Дмитрий Волков', 'dmitry@example.com');

-- Тестовые запросы
INSERT INTO requests (description, requester_id) VALUES
('Нужна мощная дрель для ремонта квартиры', 2),
('Ищу качественный перфоратор для строительных работ', 3),
('Требуется шуруповерт для сборки мебели', 4),
('Нужен лобзик для вырезания фигурных деталей', 5),
('Ищу болгарку для резки металла', 6);

-- Тестовые вещи
INSERT INTO items (name, description, is_available, owner_id, request_id) VALUES
('Дрель Makita HP2070', 'Мощная дрель с ударным механизмом, 600 Вт, режим сверления и долбления', TRUE, 1, NULL),
('Перфоратор Bosch GBH 2-26', 'Профессиональный перфоратор, 800 Вт, энергия удара 2.7 Дж', TRUE, 1, 2),
('Шуруповерт DeWalt DCD771', 'Аккумуляторный шуруповерт, 18В, быстрозажимной патрон', TRUE, 2, NULL),
('Молоток Stanley FatMax', 'Прочный молоток с фиберглассовой ручкой, вес 500г', TRUE, 3, NULL),
('Набор отверток Gross', 'Набор из 15 отверток разных размеров и типов', FALSE, 4, 1),
('Электролобзик Makita JV0600', 'Электрический лобзик для точных резов, 600 Вт', TRUE, 1, 3),
('Болгарка Einhell TH-US 240', 'Углошлифовальная машина, 2400 Вт, диаметр диска 230мм', TRUE, 5, 5),
('Шлифмашина Bosch PSS 200', 'Эксцентриковая шлифовальная машина, 200 Вт', TRUE, 6, NULL),
('Строительный фен Steinel', 'Термофен для строительных работ, 2000 Вт', TRUE, 2, 4),
('Измерительная рулетка Stanley', 'Рулетка 5 метров, металлический корпус', TRUE, 3, NULL);

-- Тестовые бронирования
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES
('2024-01-15 10:00:00', '2024-01-20 18:00:00', 1, 2, 'APPROVED'),
('2024-01-18 09:00:00', '2024-01-22 17:00:00', 3, 1, 'WAITING'),
('2024-01-25 08:00:00', '2024-01-28 16:00:00', 2, 3, 'APPROVED'),
('2024-02-01 11:00:00', '2024-02-05 19:00:00', 6, 4, 'REJECTED'),
('2024-02-10 08:00:00', '2024-02-15 20:00:00', 7, 2, 'APPROVED'),
('2024-02-12 07:00:00', '2024-02-14 15:00:00', 8, 5, 'WAITING'),
('2024-02-20 09:00:00', '2024-02-25 18:00:00', 9, 6, 'APPROVED');

-- Тестовые комментарии
INSERT INTO comments (text, item_id, author_id) VALUES
('Отличная дрель, справилась с бетонной стеной! Работает тихо и мощно.', 1, 2),
('Очень удобный шуруповерт, рекомендую. Батареи хватает надолго.', 3, 1),
('Перфоратор мощный, но тяжеловат для длительной работы.', 2, 3),
('Лобзик просто супер! Точные резы, удобная ручка.', 6, 4),
('Болгарка справляется с любым металлом. Очень доволен.', 7, 2),
('Шлифмашина легкая и удобная. Отлично подходит для финишной обработки.', 8, 5);

-- =============================================
-- Представления для удобства
-- =============================================

-- Представление доступных вещей
CREATE VIEW available_items AS
SELECT
    i.id,
    i.name,
    i.description,
    i.is_available,
    u.name as owner_name,
    u.email as owner_email
FROM items i
JOIN users u ON i.owner_id = u.id
WHERE i.is_available = TRUE;

-- Представление активных бронирований
CREATE VIEW active_bookings AS
SELECT
    b.id,
    b.start_date,
    b.end_date,
    b.status,
    i.name as item_name,
    i.description as item_description,
    owner.name as owner_name,
    booker.name as booker_name,
    booker.email as booker_email
FROM bookings b
JOIN items i ON b.item_id = i.id
JOIN users owner ON i.owner_id = owner.id
JOIN users booker ON b.booker_id = booker.id
WHERE b.status IN ('WAITING', 'APPROVED')
AND b.end_date > NOW();

-- Представление запросов с количеством предложенных вещей
CREATE VIEW requests_with_items_count AS
SELECT
    r.id,
    r.description,
    r.created,
    u.name as requester_name,
    u.email as requester_email,
    COUNT(i.id) as items_count
FROM requests r
JOIN users u ON r.requester_id = u.id
LEFT JOIN items i ON r.id = i.request_id
GROUP BY r.id, r.description, r.created, u.name, u.email;

-- =============================================
-- Статистические запросы
-- =============================================

-- Количество вещей по пользователям
SELECT
    u.name as owner_name,
    COUNT(i.id) as items_count,
    SUM(CASE WHEN i.is_available = TRUE THEN 1 ELSE 0 END) as available_items
FROM users u
LEFT JOIN items i ON u.id = i.owner_id
GROUP BY u.id, u.name
ORDER BY items_count DESC;

-- Статистика бронирований по статусам
SELECT
    status,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM bookings), 2) as percentage
FROM bookings
GROUP BY status;

-- Самые популярные вещи (по количеству бронирований)
SELECT
    i.name,
    i.description,
    COUNT(b.id) as bookings_count
FROM items i
LEFT JOIN bookings b ON i.id = b.item_id
GROUP BY i.id, i.name, i.description
ORDER BY bookings_count DESC
LIMIT 10;

-- =============================================
-- Функции и процедуры (опционально)
-- =============================================

-- Функция для проверки доступности вещи в указанный период
DELIMITER //
CREATE FUNCTION check_item_availability(
    item_id BIGINT,
    start_date TIMESTAMP,
    end_date TIMESTAMP
)
RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE is_available BOOLEAN;
    DECLARE overlapping_bookings INT;

    -- Проверяем, доступна ли вещь вообще
    SELECT is_available INTO is_available
    FROM items WHERE id = item_id;

    IF NOT is_available THEN
        RETURN FALSE;
    END IF;

    -- Проверяем, есть ли пересекающиеся бронирования
    SELECT COUNT(*) INTO overlapping_bookings
    FROM bookings
    WHERE item_id = item_id
    AND status = 'APPROVED'
    AND (
        (start_date BETWEEN start_date AND end_date) OR
        (end_date BETWEEN start_date AND end_date) OR
        (start_date <= start_date AND end_date >= end_date)
    );

    RETURN overlapping_bookings = 0;
END//
DELIMITER ;

-- Процедура для получения вещей пользователя с статистикой
DELIMITER //
CREATE PROCEDURE get_user_items_with_stats(IN user_id BIGINT)
BEGIN
    SELECT
        i.id,
        i.name,
        i.description,
        i.is_available,
        COUNT(b.id) as total_bookings,
        COUNT(CASE WHEN b.status = 'APPROVED' THEN 1 END) as approved_bookings,
        COUNT(DISTINCT c.id) as comments_count
    FROM items i
    LEFT JOIN bookings b ON i.id = b.item_id
    LEFT JOIN comments c ON i.id = c.item_id
    WHERE i.owner_id = user_id
    GROUP BY i.id, i.name, i.description, i.is_available
    ORDER BY i.created DESC;
END//
DELIMITER ;

-- =============================================
-- Примеры использования
-- =============================================

-- Пример: Проверка доступности вещи
-- SELECT check_item_availability(1, '2024-03-01 10:00:00', '2024-03-05 18:00:00') as is_available;

-- Пример: Получение вещей пользователя со статистикой
-- CALL get_user_items_with_stats(1);

-- =============================================
-- Сообщение об успешном создании
-- =============================================
SELECT 'ShareIt database schema created successfully!' as message;