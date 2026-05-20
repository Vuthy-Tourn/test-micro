-- liquibase formatted sql

-- changeset stadoor:0006-consumer_roles
CREATE TABLE IF NOT EXISTS consumer_roles
(
    id              UUID            NOT NULL,
    tenant_id       UUID            NOT NULL,
    name            VARCHAR(50)     NOT NULL,
    description     VARCHAR(255),
    status          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL,
    CONSTRAINT uq_consumer_roles_tenant_id_name UNIQUE (tenant_id, name),
    CONSTRAINT pk_consumer_roles PRIMARY KEY (id)
)

-- changeset stadoor:0006-consumer_role_mappings
CREATE TABLE IF NOT EXISTS consumer_role_mappings
(
    id                      UUID            NOT NULL,
    consumer_id             UUID            NOT NULL,
    role_id                 UUID            NOT NULL,
    assigned_at             TIMESTAMPTZ     NOT NULL,
    assigned_by_user_id     UUID            NOT NULL,
    CONSTRAINT uq_onsumer_role_mappings_consumer_id_role_id UNIQUE (consumer_id, role_id),
    CONSTRAINT pk_consumer_role_mappings PRIMARY KEY (id),
    CONSTRAINT fk_consumer_role_mappings_consumers FOREIGN KEY (consumer_id) REFERENCES consumers (id),
    CONSTRAINT fk_consumer_role_mappings_roles FOREIGN KEY (role_id) REFERENCES consumer_roles (id)

)

