-- Database schema for ReviseIt
use template_db;

-- User table
-- Use backticks for the table name as 'user' can be a reserved keyword
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Changed BIGSERIAL to BIGINT AUTO_INCREMENT for MariaDB compatibility
    email VARCHAR(255) NOT NULL UNIQUE
);

-- Bookmark table
CREATE TABLE IF NOT EXISTS bookmark (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Changed BIGSERIAL to BIGINT AUTO_INCREMENT
    user_id BIGINT NOT NULL,
    website VARCHAR(255), -- Full URL
    domain_name VARCHAR(255), -- Domain name only
    progress REAL, -- Using REAL for Float
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE -- Use backticks for referenced table
);

-- FlashCardSet table
CREATE TABLE IF NOT EXISTS flash_card_set (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Changed BIGSERIAL to BIGINT AUTO_INCREMENT
    bookmark_id BIGINT NOT NULL,
    name VARCHAR(255),
    FOREIGN KEY (bookmark_id) REFERENCES bookmark(id) ON DELETE CASCADE
);

-- FlashCard table
CREATE TABLE IF NOT EXISTS flash_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Changed BIGSERIAL to BIGINT AUTO_INCREMENT
    flash_card_set_id BIGINT NOT NULL,
    question TEXT,
    answer TEXT,
    FOREIGN KEY (flash_card_set_id) REFERENCES flash_card_set(id) ON DELETE CASCADE
);

-- QuizSet table
CREATE TABLE IF NOT EXISTS quiz_set (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Changed BIGSERIAL to BIGINT AUTO_INCREMENT
    flash_card_set_id BIGINT NOT NULL, -- Linked to FlashCardSet as per model
    name VARCHAR(255),
    FOREIGN KEY (flash_card_set_id) REFERENCES flash_card_set(id) ON DELETE CASCADE
);

-- Question table
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Changed BIGSERIAL to BIGINT AUTO_INCREMENT
    quiz_set_id BIGINT NOT NULL,
    text TEXT,
    FOREIGN KEY (quiz_set_id) REFERENCES quiz_set(id) ON DELETE CASCADE
);

-- Choice table
CREATE TABLE IF NOT EXISTS choice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Changed BIGSERIAL to BIGINT AUTO_INCREMENT
    question_id BIGINT NOT NULL,
    text TEXT,
    is_correct BOOLEAN,
    FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
);

-- Add indexes for foreign keys for potentially better performance
CREATE INDEX IF NOT EXISTS idx_bookmark_user_id ON bookmark(user_id);
CREATE INDEX IF NOT EXISTS idx_bookmark_domain_name ON bookmark(domain_name); -- Add index for domain_name
CREATE INDEX IF NOT EXISTS idx_flash_card_set_bookmark_id ON flash_card_set(bookmark_id);
CREATE INDEX IF NOT EXISTS idx_flash_card_flash_card_set_id ON flash_card(flash_card_set_id);
CREATE INDEX IF NOT EXISTS idx_quiz_set_flash_card_set_id ON quiz_set(flash_card_set_id);
CREATE INDEX IF NOT EXISTS idx_question_quiz_set_id ON question(quiz_set_id);
CREATE INDEX IF NOT EXISTS idx_choice_question_id ON choice(question_id);
