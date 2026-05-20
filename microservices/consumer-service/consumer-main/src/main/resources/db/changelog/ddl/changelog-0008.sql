-- liquibase formatted sql

-- changeset stadoor:0008-create-tenants-sync-table
CREATE TABLE IF NOT EXISTS tenants
(
    id        UUID         NOT NULL,
    name      VARCHAR(255) NOT NULL,
    synced_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_tenants PRIMARY KEY (id)
);

-- changeset stadoor:0008-create-gateways-sync-table
CREATE TABLE IF NOT EXISTS gateways
(
    id        UUID         NOT NULL,
    tenant_id UUID         NOT NULL,
    name      VARCHAR(255) NOT NULL,
    status    VARCHAR(50)  NOT NULL,
    auth_type VARCHAR(50)  NOT NULL,
    synced_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_gateways PRIMARY KEY (id)
);
