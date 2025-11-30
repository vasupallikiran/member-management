INSERT INTO role(name) VALUES('admin'), ('user');

INSERT INTO member(first_name, last_name, date_of_birth, email, created_at, updated_at)VALUES
('John', 'Doe', '1990-01-15', 'john.doe@gmail.com', now(), now()),
('Alice', 'Smith', '1990-01-15', 'alice@example.com', now(), now()),
('Bob', 'Jones', '1985-06-30', 'bob@example.com', now(), now()),
('Carol', 'Ng', '1992-09-08', 'carol.ng@example.com', now(), now());

INSERT INTO users(username, password_hash, role_id) VALUES
('admin', '$2b$12$KIXQJY5Z6hZ8eF1Oq7j0EezF1Oq7j0EezF1Oq7j0EezF1Oq7j0Ee', (SELECT id FROM role WHERE name='admin')),
('user1', '$2b$12$wHfJY5Z6hZ8eF1Oq7j0EezF1Oq7j0EezF1Oq7j0EezF1Oq7j0Ee', (SELECT id FROM role WHERE name='user'));