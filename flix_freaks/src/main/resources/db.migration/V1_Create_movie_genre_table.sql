CREATE TABLE IF NOT EXISTS genre(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS movie(
    movie_id VARCHAR(8) PRIMARY KEY,
    title VARCHAR(256),
    year INT
);

CREATE TABLE IF NOT EXISTS movie_genre(
    movie_id VARCHAR REFERENCES Movie( movie_id ) ON UPDATE CASCADE ON DELETE CASCADE,
    genre_id Int REFERENCES Genre( id ) ON UPDATE CASCADE,
    CONSTRAINT movie_genre_pk PRIMARY KEY (movie_id, genre_id)
);

CREATE TABLE IF NOT EXISTS rating(
    user_id VARCHAR(16),
    movie_id VARCHAR(16),
    rating REAL,
    rating_timestamp TIMESTAMP,
    is_explicit BOOLEAN DEFAULT true
)