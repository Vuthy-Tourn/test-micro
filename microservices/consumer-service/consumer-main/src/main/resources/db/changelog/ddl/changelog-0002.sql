-- liquibase formatted sql
-- Axon JDBC infrastructure tables — names match Axon's default EventSchema and TokenSchema.
-- PostgreSQL lowercases unquoted identifiers, so DomainEventEntry → domainevententry,
-- which Axon's generated SQL resolves to the same table.

-- changeset stadoor:0002-axon-domain-event-entry
CREATE TABLE IF NOT EXISTS DomainEventEntry
(
    globalIndex         BIGSERIAL    NOT NULL,
    eventIdentifier     VARCHAR(255) NOT NULL,
    metaData            BYTEA,
    payload             BYTEA        NOT NULL,
    payloadRevision     VARCHAR(255),
    payloadType         VARCHAR(255) NOT NULL,
    timeStamp           VARCHAR(255) NOT NULL,
    aggregateIdentifier VARCHAR(255) NOT NULL,
    sequenceNumber      BIGINT       NOT NULL,
    type                VARCHAR(255),
    CONSTRAINT pk_domainevententry PRIMARY KEY (globalIndex)
);

-- changeset stadoor:0002-axon-domain-event-unique-event
ALTER TABLE DomainEventEntry
    ADD CONSTRAINT uc_domainevententry_eventidentifier UNIQUE (eventIdentifier);

-- changeset stadoor:0002-axon-domain-event-unique-aggregate
ALTER TABLE DomainEventEntry
    ADD CONSTRAINT uc_domainevententry_aggregate UNIQUE (aggregateIdentifier, sequenceNumber, type);

-- changeset stadoor:0002-axon-snapshot-event-entry
CREATE TABLE IF NOT EXISTS SnapshotEventEntry
(
    aggregateIdentifier VARCHAR(255) NOT NULL,
    sequenceNumber      BIGINT       NOT NULL,
    type                VARCHAR(255) NOT NULL,
    eventIdentifier     VARCHAR(255) NOT NULL,
    metaData            BYTEA,
    payload             BYTEA        NOT NULL,
    payloadRevision     VARCHAR(255),
    payloadType         VARCHAR(255) NOT NULL,
    timeStamp           VARCHAR(255) NOT NULL,
    CONSTRAINT pk_snapshotevententry PRIMARY KEY (aggregateIdentifier, sequenceNumber, type)
);

-- changeset stadoor:0002-axon-snapshot-unique-event
ALTER TABLE SnapshotEventEntry
    ADD CONSTRAINT uc_snapshotevententry_eventidentifier UNIQUE (eventIdentifier);

-- changeset stadoor:0002-axon-token-entry
CREATE TABLE IF NOT EXISTS TokenEntry
(
    processorName VARCHAR(255) NOT NULL,
    segment       INTEGER      NOT NULL,
    token         BYTEA,
    tokenType     VARCHAR(255),
    timestamp     VARCHAR(255) NOT NULL,
    owner         VARCHAR(255),
    CONSTRAINT pk_tokenentry PRIMARY KEY (processorName, segment)
);

-- changeset stadoor:0002-axon-dead-letter-entry
CREATE TABLE IF NOT EXISTS dead_letter_entry
(
    dead_letter_id       VARCHAR(255)  NOT NULL,
    processing_group     VARCHAR(255),
    sequence_identifier  VARCHAR(255),
    sequence_index       BIGINT        NOT NULL,
    enqueued_at          TIMESTAMPTZ,
    last_touched         TIMESTAMPTZ,
    processing_started   TIMESTAMPTZ,
    cause_type           VARCHAR(255),
    cause_message        VARCHAR(1023),
    diagnostics          BYTEA,
    message_type         VARCHAR(255),
    event_identifier     VARCHAR(255)  NOT NULL,
    time_stamp           VARCHAR(255),
    payload_type         VARCHAR(255),
    payload_revision     VARCHAR(255),
    payload              BYTEA,
    meta_data            BYTEA,
    type                 VARCHAR(255),
    aggregate_identifier VARCHAR(255),
    sequence_number      BIGINT,
    token_type           VARCHAR(255),
    token                BYTEA,
    CONSTRAINT pk_deadletterentry PRIMARY KEY (dead_letter_id)
);
