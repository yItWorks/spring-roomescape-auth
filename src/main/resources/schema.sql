CREATE TABLE IF NOT EXISTS reservation_time
(
    id        BIGINT      NOT NULL AUTO_INCREMENT,
    start_at  Time        NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS theme
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    url         VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS store
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS member
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    login_id VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    role     VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_member_login_id UNIQUE (login_id)
);

CREATE TABLE IF NOT EXISTS manager
(
    id        BIGINT    NOT NULL AUTO_INCREMENT,
    member_id BIGINT    NOT NULL,
    store_id  BIGINT    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (store_id)  REFERENCES store  (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reservation
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    date      DATE         NOT NULL,
    time_id   BIGINT       NOT NULL,
    theme_id  BIGINT       NOT NULL,
    member_id BIGINT       NOT NULL,
    store_id  BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_reservation_date_time_theme_store UNIQUE (date, time_id, theme_id, store_id),
    FOREIGN KEY (time_id)   REFERENCES reservation_time (id),
    FOREIGN KEY (theme_id)  REFERENCES theme (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (store_id)  REFERENCES store (id)
);
