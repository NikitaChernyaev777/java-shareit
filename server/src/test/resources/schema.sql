DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS item_requests CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name  VARCHAR(255)                        NOT NULL,
    email VARCHAR(255)                        NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    description  VARCHAR(2000)                       NOT NULL,
    requestor_id BIGINT                              NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE         NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_item_requests PRIMARY KEY (id),
    CONSTRAINT fk_item_requests_requestor_id FOREIGN KEY (requestor_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name        VARCHAR(255)                        NOT NULL,
    description VARCHAR(2000)                       NOT NULL,
    available   BOOLEAN                             NOT NULL,
    owner_id    BIGINT                              NOT NULL,
    request_id  BIGINT,
    CONSTRAINT pk_items PRIMARY KEY (id),
    CONSTRAINT fk_items_owner_id FOREIGN KEY (owner_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_items_request_id FOREIGN KEY (request_id) REFERENCES item_requests (id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    text      VARCHAR(2000)                       NOT NULL,
    item_id   BIGINT                              NOT NULL,
    author_id BIGINT                              NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE         NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES items (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE         NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE         NOT NULL,
    item_id    BIGINT                              NOT NULL,
    booker_id  BIGINT                              NOT NULL,
    status     VARCHAR(20)                         NOT NULL,
    CONSTRAINT pk_bookings PRIMARY KEY (id),
    CONSTRAINT fk_bookings_item_id FOREIGN KEY (item_id) REFERENCES items (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_bookings_booker_id FOREIGN KEY (booker_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT ck_bookings_dates CHECK (end_date > start_date)
);