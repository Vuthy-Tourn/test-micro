-- liquibase formatted sql

-- changeset stadoor:0001-consumers
CREATE TABLE IF NOT EXISTS consumers
(
    id          UUID         NOT NULL,
    tenant_id   UUID         NOT NULL,
    gateway_id  UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    type        VARCHAR(50)  NOT NULL,
    auth_type   VARCHAR(50)  NOT NULL,
    status      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_consumers PRIMARY KEY (id)
);

-- changeset stadoor:0001-consumer-credentials
CREATE TABLE IF NOT EXISTS consumer_credentials
(
    id              UUID        NOT NULL,
    consumer_id     UUID        NOT NULL,
    tenant_id       UUID        NOT NULL,
    credential_type VARCHAR(50) NOT NULL,
    status          BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_consumer_credentials PRIMARY KEY (id),
    CONSTRAINT fk_consumer_credentials_consumer FOREIGN KEY (consumer_id) REFERENCES consumers (id)
);

-- changeset stadoor:0001-basic-auth-credentials
CREATE TABLE IF NOT EXISTS basic_auth_credentials
(
    id                     UUID         NOT NULL,
    consumer_credential_id UUID         NOT NULL,
    username               VARCHAR(255) NOT NULL,
    password_hash          VARCHAR(255) NOT NULL,
    created_at             TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_basic_auth_credentials PRIMARY KEY (id),
    CONSTRAINT fk_basic_auth_credential FOREIGN KEY (consumer_credential_id) REFERENCES consumer_credentials (id)
);

-- changeset stadoor:0001-api-key-credentials
CREATE TABLE IF NOT EXISTS api_key_credentials
(
    id                     UUID         NOT NULL,
    consumer_credential_id UUID         NOT NULL,
    api_key                VARCHAR(255) NOT NULL,
    key_hash               VARCHAR(255) NOT NULL,
    created_at             TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_api_key_credentials PRIMARY KEY (id),
    CONSTRAINT fk_api_key_credential FOREIGN KEY (consumer_credential_id) REFERENCES consumer_credentials (id)
);

-- changeset stadoor:0001-jwt-credentials
CREATE TABLE IF NOT EXISTS jwt_credentials
(
    id                     UUID         NOT NULL,
    consumer_credential_id UUID         NOT NULL,
    username               VARCHAR(255),
    password_hash          VARCHAR(255),
    secret_key             TEXT         NOT NULL,
    algorithm              VARCHAR(20)  NOT NULL DEFAULT 'HS256',
    access_token_ttl       INT          NOT NULL DEFAULT 3600,
    refresh_token_ttl      INT          NOT NULL DEFAULT 86400,
    created_at             TIMESTAMPTZ  NOT NULL,
    updated_at             TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_jwt_credentials PRIMARY KEY (id),
    CONSTRAINT fk_jwt_credential FOREIGN KEY (consumer_credential_id) REFERENCES consumer_credentials (id)
);
