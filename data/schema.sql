-- -----------------------------------------------------
-- Schema Project 2
-- -----------------------------------------------------
# DROP SCHEMA IF EXISTS p2;
# CREATE SCHEMA p2;
# USE p2;

-- -----------------------------------------------------
-- Set timezone
-- -----------------------------------------------------
# SET time_zone = 'Pacific/Auckland';
SET time_zone = 'UTC';

-- -----------------------------------------------------
-- Table locations
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS locations
(
    location_id INT            NOT NULL AUTO_INCREMENT,
    location    VARCHAR(250)    NOT NULL,
    latitude    DECIMAL(11, 8) NOT NULL,
    longitude   DECIMAL(11, 8) NOT NULL,
    PRIMARY KEY (`location_id`)
);

-- -----------------------------------------------------
-- Table users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS users
(
    user_id          INT                                       NOT NULL AUTO_INCREMENT,
    username         VARCHAR(50)                               NOT NULL UNIQUE,
    first_name       VARCHAR(50)                               NOT NULL DEFAULT '',
    last_name        VARCHAR(50)                               NOT NULL DEFAULT '',
    location_id      INT,
    email            VARCHAR(320)                              NOT NULL UNIQUE COMMENT 'Maximum email address length according to RFC5321 section 4.5.3.1 is 320 characters (64 for local-part, 1 for at sign, 255 for domain)',
    description      text                                      NOT NULL,
    profile_image    VARCHAR(255)                              NOT NULL DEFAULT '',
    password_hash    CHAR(64)                                  NOT NULL COMMENT 'SHA256 password hash stored in hexadecimal (64 characters)',
    role             ENUM ('voter', 'siteHelper', 'siteAdmin') NOT NULL DEFAULT 'voter',
    status           ENUM ('active', 'inactive')               NOT NULL DEFAULT 'active',
    created_at       DATETIME                                  NOT NULL DEFAULT NOW(),
    display_location TINYINT                                   NOT NULL DEFAULT 0,
    user_city_name    VARCHAR(250),
    user_city_latitude    DECIMAL(11, 8),
    user_city_longitude   DECIMAL(11, 8),
    PRIMARY KEY (`user_id`),
    FOREIGN KEY (`location_id`) REFERENCES `locations` (`location_id`)
);

-- -----------------------------------------------------
-- Table themes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS themes
(
    theme_id          INT          NOT NULL AUTO_INCREMENT,
    theme_name        VARCHAR(128) NOT NULL,
    theme_description TEXT         NOT NULL,
    created_by        INT          NOT NULL,
    created_at        DATETIME     NOT NULL DEFAULT NOW(),
    approved_by       INT,
    approved_at       DATETIME,
    approved          TINYINT(1)   NOT NULL DEFAULT 0,
    accepted          TINYINT(1),
    enable_location   TINYINT(1)   NOT NULL DEFAULT 0,

    PRIMARY KEY (`theme_id`),
    FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`approved_by`) REFERENCES `users` (`user_id`)
);

################## Donation ##################
-- -----------------------------------------------------
-- Table charities
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS charities
(
    charity_id          INT          NOT NULL AUTO_INCREMENT,
    charity_image       VARCHAR(255) NOT NULL,
    charity_name        VARCHAR(128) NOT NULL,
    charity_description TEXT         NOT NULL,
    reg_num             VARCHAR(20)  NOT NULL,
    bank_acc            VARCHAR(20)  NOT NULL,
    ird_num             VARCHAR(20)  NOT NULL,
    create_by           INT          NOT NULL,
    created_at          DATETIME     NOT NULL DEFAULT NOW(),
    theme_id            INT          NOT NULL,
    approved_by         INT,
    approved_at         DATETIME,
    approved            TINYINT(1),

    PRIMARY KEY (`charity_id`),
    FOREIGN KEY (`theme_id`) REFERENCES `themes` (`theme_id`),
    FOREIGN KEY (`create_by`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`approved_by`) REFERENCES `users` (`user_id`)
);

-- -----------------------------------------------------
-- Table donations
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS donations
(
    donation_id INT        NOT NULL AUTO_INCREMENT,
    charity_id  INT        NOT NULL,
    succeed     TINYINT(1) NOT NULL DEFAULT 0,
    donated_by  INT        NOT NULL,
    donated_at  DATETIME   NOT NULL DEFAULT NOW(),
    amount      FLOAT      NOT NULL,
    card_num    char(20)   NOT NULL,
    message     char(255),

    PRIMARY KEY (`donation_id`),
    FOREIGN KEY (`charity_id`) REFERENCES `charities` (`charity_id`),
    FOREIGN KEY (`donated_by`) REFERENCES `users` (`user_id`)
);

################## Donation ##################


-- -----------------------------------------------------
-- Table events
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS events
(
    event_id    INT                                    NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255)                           NOT NULL,
    description TEXT                                   NOT NULL,
    image       VARCHAR(255)                           NOT NULL,
    start_date  DATETIME                               NOT NULL,
    end_date    DATETIME                               NOT NULL,
    status      ENUM ('draft', 'published','verified') NOT NULL,
    create_by   INT                                    NOT NULL,
    created_at  DATETIME                               NOT NULL DEFAULT NOW(),
    theme_id    INT                                    NOT NULL,
    verified_by INT,
    verified_at DATETIME,
    verified    TINYINT(1)                             NOT NULL DEFAULT 0,

    PRIMARY KEY (`event_id`),
    FOREIGN KEY (`theme_id`) REFERENCES `themes` (`theme_id`),
    FOREIGN KEY (`create_by`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`verified_by`) REFERENCES `users` (`user_id`)
);

-- -----------------------------------------------------
-- Table candidates
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS candidates
(
    candidate_id INT          NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    image        VARCHAR(255) NOT NULL,
    location_id  INT,
    event_id     INT          NOT NULL,
    author       VARCHAR(50)  NOT NULL,
    create_by    INT          NOT NULL,
    created_at   DATETIME     NOT NULL DEFAULT NOW(),

    PRIMARY KEY (`candidate_id`),
    FOREIGN KEY (`create_by`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`location_id`) REFERENCES `locations` (`location_id`),
    FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Table votes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS votes
(
    vote_id      INT         NOT NULL AUTO_INCREMENT,
    event_id     INT         NOT NULL,
    candidate_id INT         NOT NULL,
    voted_by     INT         NOT NULL,
    voted_ip     VARCHAR(64) NOT NULL,
    voted_at     DATETIME    NOT NULL DEFAULT NOW(),
    valid        TINYINT(1)  NOT NULL DEFAULT true,

    PRIMARY KEY (`vote_id`),
    FOREIGN KEY (`candidate_id`) REFERENCES `candidates` (`candidate_id`) ON DELETE CASCADE,
    FOREIGN KEY (`voted_by`) REFERENCES `users` (`user_id`),
    UNIQUE KEY `uniq_comp_voter` (`event_id`, `voted_by`)
);

-- -----------------------------------------------------
-- Table announcements
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS announcements
(
    announcement_id INT                         NOT NULL AUTO_INCREMENT,
    title           varchar(128)                NOT NULL,
    content         text                        NOT NULL,
    end_at          datetime                    NOT NULL,
    status          ENUM ('active', 'inactive') NOT NULL,
    created_by      int                         NOT NULL,
    created_at      DATETIME                    NOT NULL DEFAULT NOW(),

    PRIMARY KEY (`announcement_id`),
    FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`)
);

-- -----------------------------------------------------
-- Table theme_roles
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS theme_roles
(
    theme_role_id INT                           NOT NULL AUTO_INCREMENT,
    user_id       INT                           NOT NULL,
    theme_id      INT                           NOT NULL,
    role          ENUM ('tScrutineer','tAdmin') NOT NULL DEFAULT 'tScrutineer',
    created_by    INT                           NOT NULL,
    created_at    DATETIME                      NOT NULL DEFAULT NOW(),

    PRIMARY KEY (`theme_role_id`),
    UNIQUE KEY `uniq_user_theme` (`user_id`, `theme_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`theme_id`) REFERENCES `themes` (`theme_id`)
);

# -- -----------------------------------------------------
# -- View theme_mgmt_group
# -- -----------------------------------------------------
# CREATE OR REPLACE VIEW  theme_mgmt_group AS
# SELECT * FROM users u
# RIGHT JOIN theme_roles t
# ON u.user_id = t.created_by;

-- -----------------------------------------------------
-- Table banned_users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS banned_users
(
    ban_id        INT        NOT NULL AUTO_INCREMENT,
    user_id       INT        NOT NULL,
    banned_reason TEXT       NOT NULL,
    banned_by     INT        NOT NULL,
    banned_at     DATETIME   NOT NULL DEFAULT NOW(),
    revoked_by    INT,
    revoked_at    DATETIME,
    theme_id      INT,
    revoked       TINYINT(1) NOT NULL DEFAULT 0,

    PRIMARY KEY (`ban_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`banned_by`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`revoked_by`) REFERENCES `users` (`user_id`)
);

-- -----------------------------------------------------
-- Table appeals
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS appeals
(
    appeal_id       INT        NOT NULL AUTO_INCREMENT,
    ban_id          INT        NOT NULL,
    appealed_reason TEXT       NOT NULL,
    appealed_at     DATETIME   NOT NULL DEFAULT NOW(),
    processed       TINYINT(1) NOT NULL DEFAULT 0,
    processed_by    INT,
    processed_at    DATETIME,
    accepted        TINYINT(1),

    PRIMARY KEY (`appeal_id`),
    FOREIGN KEY (`ban_id`) REFERENCES `banned_users` (`ban_id`)
);

-- -----------------------------------------------------
-- View banned_users_with_appeals
-- -----------------------------------------------------
CREATE OR REPLACE VIEW banned_users_with_appeals AS
SELECT b.ban_id,
       b.user_id,
       b.banned_reason,
       b.banned_by,
       b.banned_at,
       b.revoked_by,
       b.revoked_at,
       b.theme_id,
       b.revoked,
       a.appeal_id,
       a.appealed_reason,
       a.appealed_at
FROM banned_users b
         LEFT JOIN appeals a ON b.ban_id = a.ban_id;

################## TECH SUPPORT ##################
-- -----------------------------------------------------
-- Table tickets
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tickets
(
    ticket_id  INT                           NOT NULL AUTO_INCREMENT,
    subject    VARCHAR(255)                  NOT NULL,
    content    TEXT                          NOT NULL,
    status     ENUM ('new', 'open','closed') NOT NULL DEFAULT 'new',
    solution   ENUM ('cancelled','drop', 'resolved'),
    created_by INT                           NOT NULL,
    created_at DATETIME                      NOT NULL DEFAULT NOW(),
    assign_to  INT,
    updated_by INT,
    updated_at DATETIME,

    PRIMARY KEY (`ticket_id`),
    FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`assign_to`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`updated_by`) REFERENCES `users` (`user_id`)
);

-- -----------------------------------------------------
-- Table comments
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS comments
(
    comment_id INT      NOT NULL AUTO_INCREMENT,
    ticket_id  INT      NOT NULL,
    content    TEXT     NOT NULL,
    created_by INT      NOT NULL,
    created_at DATETIME NOT NULL DEFAULT NOW(),

    PRIMARY KEY (`comment_id`),
    FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
    FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`)
);


CREATE TABLE IF NOT EXISTS `notification`
(
    `notification_id` INT          NOT NULL AUTO_INCREMENT,
    `user_id`         INT          NOT NULL,
    `content`         VARCHAR(255) NOT NULL,
    `url`             VARCHAR(255)          DEFAULT NULL,
    `created_at`      DATETIME     NOT NULL DEFAULT NOW(),
    `has_read`        BOOL         NOT NULL DEFAULT 0,
    `read_at`         DATETIME,
    PRIMARY KEY (`notification_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),

    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_created_at` (`created_at`)
);

-- -----------------------------------------------------
-- View events_with_candidates_and_locations
-- -----------------------------------------------------
CREATE OR REPLACE VIEW events_with_candidates_and_locations AS
SELECT e.event_id,
       e.name        AS event_name,
       e.description AS event_description,
       e.image       AS event_image,
       e.start_date,
       e.end_date,
       e.status      AS event_status,
       e.create_by   AS event_created_by,
       e.created_at  AS event_created_at,
       e.theme_id,
       e.verified_by AS event_verified_by,
       e.verified_at AS event_verified_at,
       e.verified    AS event_verified,
       c.candidate_id,
       c.name        AS candidate_name,
       c.description AS candidate_description,
       c.image       AS candidate_image,
       c.location_id,
       c.author      AS candidate_author,
       c.create_by   AS candidate_created_by,
       c.created_at  AS candidate_created_at,
       l.location    AS candidate_location,
       l.latitude    AS candidate_latitude,
       l.longitude   AS candidate_longitude
FROM events e
         LEFT JOIN candidates c ON e.event_id = c.event_id
         LEFT JOIN locations l ON c.location_id = l.location_id;

-- -----------------------------------------------------
-- View users_with_location_with_theme_roles
-- -----------------------------------------------------
CREATE OR REPLACE VIEW users_with_theme_roles AS
SELECT u.user_id,
       u.username,
       u.first_name,
       u.last_name,
       u.email,
       u.description,
       u.profile_image,
       u.password_hash,
       u.role        AS site_role,
       u.status,
       u.created_at,
       l.location    AS user_location,
       l.latitude,
       l.longitude,
       tr.theme_id,
       tr.role       AS theme_role,
       tr.created_by AS theme_role_created_by,
       tr.created_at AS theme_role_created_at
FROM users u
         LEFT JOIN locations l ON u.location_id = l.location_id
         LEFT JOIN theme_roles tr ON u.user_id = tr.user_id;

CREATE OR REPLACE VIEW tickets_management_view AS
SELECT t.ticket_id,
       t.subject,
       t.content,
       t.status,
       t.solution,
       t.created_by,
       t.created_at,
       t.assign_to,
       t.updated_by,
       t.updated_at,
       u1.username AS created_by_username,
       u2.username AS assign_to_username,
       u3.username AS updated_by_username
FROM tickets t
         LEFT JOIN
     users u1 ON t.created_by = u1.user_id
         LEFT JOIN
     users u2 ON t.assign_to = u2.user_id
         LEFT JOIN
     users u3 ON t.updated_by = u3.user_id;

CREATE OR REPLACE VIEW comments_management_view AS
SELECT c.comment_id,
       c.ticket_id,
       c.content,
       c.created_by,
       c.created_at,
       u.username AS created_by_username
FROM comments c
         LEFT JOIN
     users u ON c.created_by = u.user_id;
