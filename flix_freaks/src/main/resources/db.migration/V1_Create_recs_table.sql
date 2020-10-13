-- CREATE TABLE IF NOT EXISTS version(
--     version SERIAL,
--     type VARCHAR,
--     date TIMESTAMP
-- );

CREATE TABLE IF NOT EXISTS seed_recs(
    created TIMESTAMP,
    source VARCHAR,
    target VARCHAR,
    support NUMERIC,
    confidence NUMERIC,
    type VARCHAR,
    PRIMARY KEY (created, source, target)
--     version INT REFERENCES Version( version ) ON UPDATE CASCADE ON DELETE CASCADE
);