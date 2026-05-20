-- liquibase formatted sql

-- changeset ThongFazon:1775464618771-1
CREATE TABLE gateways
(
    gateway_id   UUID NOT NULL,
    developer_id UUID,
    gateway_name VARCHAR(255),
    description  VARCHAR(255),
    status       VARCHAR(255),
    auth_type    VARCHAR(255),
    gateway_type VARCHAR(255),
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_gateways PRIMARY KEY (gateway_id)
);

-- changeset ThongFazon:1775464618771-2
CREATE TABLE routes
(
    route_id    UUID NOT NULL,
    gateway_id  UUID,
    service_id  UUID,
    route_path  VARCHAR(255),
    target_path VARCHAR(255),
    method      VARCHAR(255),
    status      VARCHAR(255),
    secure      VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_routes PRIMARY KEY (route_id)
);

-- changeset ThongFazon:1775464618771-4
CREATE TABLE services
(
    service_id   UUID NOT NULL,
    gateway_id   UUID,
    name         VARCHAR(255),
    service_type VARCHAR(255),
    status       VARCHAR(255),
    base_url     VARCHAR(255),
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_service PRIMARY KEY (service_id)
);

-- changeset ThongFazon:1775464618771-3
CREATE TABLE gateway_security_config
(
    gateway_id UUID NOT NULL,
    auth_type  VARCHAR(50),
    config     JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_gateway_security_config PRIMARY KEY (gateway_id),
    CONSTRAINT fk_gateway_security_config_gateway
        FOREIGN KEY (gateway_id) REFERENCES gateways (gateway_id)
);

