-- liquibase formatted sql

-- changeset stadoor:0009-alter-consumers-status-to-varchar
ALTER TABLE consumers ALTER COLUMN status TYPE VARCHAR(50) USING CASE WHEN status THEN 'ACTIVE' ELSE 'INACTIVE' END;