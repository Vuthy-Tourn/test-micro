-- liquibase formatted sql

-- changeset stadoor:0005-refresh_tokens
CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id                  UUID            NOT NULL,
    jwt_credential_id   UUID            NOT NULL,
    token_hash          VARCHAR(255)    NOT NULL,
    expires_at          TIMESTAMPTZ     NOT NULL,
    revoked             BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ     NOT NULL,
    CONSTRAINT uq_refresh_token_hash UNIQUE (token_hash),
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT fk_refresh_tokens FOREIGN KEY (jwt_credential_id) REFERENCES jwt_credentials (id)
)
