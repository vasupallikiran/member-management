-- Roles
INSERT INTO role(id, name) VALUES
                               (gen_random_uuid(), 'admin'),
                               (gen_random_uuid(), 'user');

-- Members
INSERT INTO member(id, first_name, last_name, date_of_birth, email, created_at, updated_at, version)
VALUES
    (gen_random_uuid(), 'John', 'Doe', '1990-01-15', 'john.doe@gmail.com', now(), now(), 0),
    (gen_random_uuid(), 'Alice', 'Smith', '1990-01-15', 'alice@example.com', now(), now(), 0),
    (gen_random_uuid(), 'Bob', 'Jones', '1985-06-30', 'bob@example.com', now(), now(), 0),
    (gen_random_uuid(), 'Carol', 'Ng', '1992-09-08', 'carol.ng@example.com', now(), now(), 0);

-- Users
INSERT INTO users(id, username, password_hash, role_id, version)
VALUES
    (gen_random_uuid(), 'admin', '$2b$12$KIXQJY5Z6hZ8eF1Oq7j0EezF1Oq7j0EezF1Oq7j0EezF1Oq7j0Ee', (SELECT id FROM role WHERE name='admin'), 0),
    (gen_random_uuid(), 'user1', '$2b$12$wHfJY5Z6hZ8eF1Oq7j0EezF1Oq7j0EezF1Oq7j0EezF1Oq7j0Ee', (SELECT id FROM role WHERE name='user'), 0);
