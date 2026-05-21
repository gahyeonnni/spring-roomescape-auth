DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS theme;
DROP TABLE IF EXISTS store;
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS reservation_time;

CREATE TABLE reservation_time
(
    id        BIGINT NOT NULL AUTO_INCREMENT,
    start_at  TIME   NOT NULL,
    finish_at TIME   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE "user"
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(50)  NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id)
);

CREATE TABLE store
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    manager_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (manager_id) REFERENCES "user" (id)
);

CREATE TABLE theme
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    image_url   VARCHAR(255) NOT NULL,
    store_id    BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (store_id) REFERENCES store (id)
);

CREATE TABLE reservation
(
    id        BIGINT NOT NULL AUTO_INCREMENT,
    date      DATE   NOT NULL,
    time_id   BIGINT,
    theme_id  BIGINT,
    member_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (time_id) REFERENCES reservation_time (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id),
    FOREIGN KEY (member_id) REFERENCES "user" (id)
);