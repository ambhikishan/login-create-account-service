CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       verified BOOLEAN DEFAULT FALSE
);
INSERT INTO users (username, password, email)
VALUES
    ('john', '$2a$10$Jj1ApKdXh3Ssx3.ESnOX..e9Cif8fUk4KN0iK9E2KkYZBk5GdJe1e', 'john@gmail.com')
