-- liquibase formatted sql

-- changeset stadoor:0007-make-jwt-secret-key-nullable
ALTER TABLE jwt_credentials ALTER COLUMN secret_key DROP NOT NULL;
