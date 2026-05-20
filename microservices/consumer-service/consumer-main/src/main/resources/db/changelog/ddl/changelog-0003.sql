-- liquibase formatted sql

-- changeset stadoor:0003-rename-domain-event-table splitStatements:false
DO $$
BEGIN
    IF to_regclass('public.domainevententry') IS NOT NULL
       AND to_regclass('public.domain_event_entry') IS NULL THEN
        ALTER TABLE public.domainevententry RENAME TO domain_event_entry;
    END IF;
END $$;

-- changeset stadoor:0003-rename-domain-event-columns splitStatements:false
DO $$
BEGIN
    IF to_regclass('public.domain_event_entry') IS NOT NULL THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'domain_event_entry'
              AND column_name = 'globalindex'
        ) THEN
            ALTER TABLE public.domain_event_entry RENAME COLUMN globalindex TO global_index;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'domain_event_entry'
              AND column_name = 'eventidentifier'
        ) THEN
            ALTER TABLE public.domain_event_entry RENAME COLUMN eventidentifier TO event_identifier;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'domain_event_entry'
              AND column_name = 'metadata'
        ) THEN
            ALTER TABLE public.domain_event_entry RENAME COLUMN metadata TO meta_data;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'domain_event_entry'
              AND column_name = 'payloadrevision'
        ) THEN
            ALTER TABLE public.domain_event_entry RENAME COLUMN payloadrevision TO payload_revision;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'domain_event_entry'
              AND column_name = 'payloadtype'
        ) THEN
            ALTER TABLE public.domain_event_entry RENAME COLUMN payloadtype TO payload_type;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'domain_event_entry'
              AND column_name = 'timestamp'
        ) THEN
            ALTER TABLE public.domain_event_entry RENAME COLUMN "timestamp" TO time_stamp;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'domain_event_entry'
              AND column_name = 'aggregateidentifier'
        ) THEN
            ALTER TABLE public.domain_event_entry RENAME COLUMN aggregateidentifier TO aggregate_identifier;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'domain_event_entry'
              AND column_name = 'sequencenumber'
        ) THEN
            ALTER TABLE public.domain_event_entry RENAME COLUMN sequencenumber TO sequence_number;
        END IF;
    END IF;
END $$;

-- changeset stadoor:0003-rename-snapshot-event-table splitStatements:false
DO $$
BEGIN
    IF to_regclass('public.snapshotevententry') IS NOT NULL
       AND to_regclass('public.snapshot_event_entry') IS NULL THEN
        ALTER TABLE public.snapshotevententry RENAME TO snapshot_event_entry;
    END IF;
END $$;

-- changeset stadoor:0003-rename-snapshot-event-columns splitStatements:false
DO $$
BEGIN
    IF to_regclass('public.snapshot_event_entry') IS NOT NULL THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'snapshot_event_entry'
              AND column_name = 'aggregateidentifier'
        ) THEN
            ALTER TABLE public.snapshot_event_entry RENAME COLUMN aggregateidentifier TO aggregate_identifier;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'snapshot_event_entry'
              AND column_name = 'sequencenumber'
        ) THEN
            ALTER TABLE public.snapshot_event_entry RENAME COLUMN sequencenumber TO sequence_number;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'snapshot_event_entry'
              AND column_name = 'eventidentifier'
        ) THEN
            ALTER TABLE public.snapshot_event_entry RENAME COLUMN eventidentifier TO event_identifier;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'snapshot_event_entry'
              AND column_name = 'metadata'
        ) THEN
            ALTER TABLE public.snapshot_event_entry RENAME COLUMN metadata TO meta_data;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'snapshot_event_entry'
              AND column_name = 'payloadrevision'
        ) THEN
            ALTER TABLE public.snapshot_event_entry RENAME COLUMN payloadrevision TO payload_revision;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'snapshot_event_entry'
              AND column_name = 'payloadtype'
        ) THEN
            ALTER TABLE public.snapshot_event_entry RENAME COLUMN payloadtype TO payload_type;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'snapshot_event_entry'
              AND column_name = 'timestamp'
        ) THEN
            ALTER TABLE public.snapshot_event_entry RENAME COLUMN "timestamp" TO time_stamp;
        END IF;
    END IF;
END $$;

-- changeset stadoor:0003-rename-token-table splitStatements:false
DO $$
BEGIN
    IF to_regclass('public.tokenentry') IS NOT NULL
       AND to_regclass('public.token_entry') IS NULL THEN
        ALTER TABLE public.tokenentry RENAME TO token_entry;
    END IF;
END $$;

-- changeset stadoor:0003-rename-token-columns splitStatements:false
DO $$
BEGIN
    IF to_regclass('public.token_entry') IS NOT NULL THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'token_entry'
              AND column_name = 'processorname'
        ) THEN
            ALTER TABLE public.token_entry RENAME COLUMN processorname TO processor_name;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'token_entry'
              AND column_name = 'tokentype'
        ) THEN
            ALTER TABLE public.token_entry RENAME COLUMN tokentype TO token_type;
        END IF;
    END IF;
END $$;

-- changeset stadoor:0003-rename-domain-event-sequence splitStatements:false
DO $$
BEGIN
    IF to_regclass('public.domainevententry_globalindex_seq') IS NOT NULL
       AND to_regclass('public.domain_event_entry_seq') IS NULL THEN
        ALTER SEQUENCE public.domainevententry_globalindex_seq RENAME TO domain_event_entry_seq;
    END IF;
END $$;

-- changeset stadoor:0003-set-domain-event-default-sequence splitStatements:false
DO $$
BEGIN
    IF to_regclass('public.domain_event_entry') IS NOT NULL
       AND to_regclass('public.domain_event_entry_seq') IS NOT NULL THEN
        ALTER TABLE public.domain_event_entry
            ALTER COLUMN global_index SET DEFAULT nextval('domain_event_entry_seq');
    END IF;
END $$;
