CREATE TABLE commands
(
    id BIGSERIAL,
    endpoint VARCHAR(255),
    method INTEGER,
    name VARCHAR(255)
);
CREATE UNIQUE INDEX uk_py1b1n9yenhsrtlh4gj3p7b4g ON commands (name);
