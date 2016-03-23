ALTER TABLE commands ADD PRIMARY KEY (id);

CREATE TABLE sub_commands
(
    id BIGSERIAL PRIMARY KEY,
    endpoint VARCHAR(255),
    command_id bigint,
    method VARCHAR(255),
    name VARCHAR(255),
    CONSTRAINT command_id FOREIGN KEY (command_id) REFERENCES commands (id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE sub_command_arguments
(
    name VARCHAR(255),
    value VARCHAR(4096),
    sub_command_id bigint,
    CONSTRAINT pk PRIMARY KEY (sub_command_id, name)
);
