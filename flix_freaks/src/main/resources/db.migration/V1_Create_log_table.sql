CREATE TABLE IF NOT EXISTS log(
    id SERIAL PRIMARY KEY,
    created TIMESTAMP,
    user_id VARCHAR,
    content_id VARCHAR(16),
    event VARCHAR(16),
    session_id VARCHAR(128)
);