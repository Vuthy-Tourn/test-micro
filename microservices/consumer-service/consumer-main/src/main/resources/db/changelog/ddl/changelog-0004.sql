-- liquibase formatted sql

-- changeset stadoor:0004-add-gateway-id-to-jwt-credentials
ALTER TABLE jwt_credentials ADD COLUMN gateway_id UUID;

-- changeset stadoor:0004-unique-jwt-username-per-gateway
ALTER TABLE jwt_credentials
ADD CONSTRAINT uq_jwt_username_gateway UNIQUE (username, gateway_id);