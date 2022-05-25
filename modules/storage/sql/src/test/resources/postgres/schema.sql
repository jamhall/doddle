-- noinspection SqlResolveForFile @ object-type/"doddle_job_state"

/**
 * Create a new postgres enum type for the job state
 */
CREATE TYPE doddle_job_state AS ENUM (
    'available',
    'scheduled',
    'executing',
    'retryable',
    'completed',
    'discarded',
    'failed'
);

CREATE TYPE doddle_job_category AS ENUM (
    'standard',
    'scheduled'
);

/**
 * Represents a queue in the system
 */
 CREATE TABLE IF NOT EXISTS doddle_queue (
    id        VARCHAR(36) NOT NULL PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    priority  NUMERIC (3, 2) NOT NULL,
    locked_at TIMESTAMP
);

/**
 * Represents a job in the system
 */
CREATE UNIQUE INDEX IF NOT EXISTS doddle_job_id_uidx ON doddle_job (id);

/**
 * Create an index for the job primary key
 */
CREATE TABLE IF NOT EXISTS doddle_job
(
    id                     VARCHAR(36)          NOT NULL PRIMARY KEY,
    queue_id               VARCHAR(36)          NOT NULL REFERENCES doddle_queue (id) ON DELETE CASCADE,
    name                   VARCHAR(250)         NOT NULL,
    identifier             VARCHAR(250)         UNIQUE,
    category               doddle_job_category  NOT NULL,
    handler                VARCHAR(150)         NOT NULL,
    payload                TEXT,
    state                  doddle_job_state     NOT NULL,
    created_at             TIMESTAMP            NOT NULL DEFAULT NOW(),
    completed_at           TIMESTAMP,
    scheduled_at           TIMESTAMP            NOT NULL DEFAULT NOW(),
    discarded_at           TIMESTAMP,
    executing_at           TIMESTAMP,
    failed_at              TIMESTAMP,
    max_retries            INTEGER              NOT NULL,
    retries                INTEGER              NOT NULL DEFAULT 0,
    timeout                BIGINT               NOT NULL,
    tags                   TEXT[],
    progress_max_value     INTEGER,
    progress_current_value INTEGER,
    error_message          VARCHAR(2000),
    error_throwable        VARCHAR(1000),
    error_stack_trace      TEXT
);

/**
 * Create an index for the job state
 */
CREATE INDEX IF NOT EXISTS doddle_job_state_idx ON doddle_job (state);

/**
 * Create an index for the job category
 */
CREATE INDEX IF NOT EXISTS doddle_job_category_idx ON doddle_job (category);

/**
 * Create an index for the job scheduled_at
 */
CREATE INDEX IF NOT EXISTS doddle_job_scheduled_at_idx ON doddle_job (scheduled_at);

/**
 * Represents a log message for a given job
 */
CREATE TABLE IF NOT EXISTS doddle_job_message
(
    id                VARCHAR(36) NOT NULL PRIMARY KEY,
    job_id            VARCHAR(36) NOT NULL REFERENCES doddle_job (id) ON DELETE CASCADE,
    created_at        TIMESTAMP   NOT NULL DEFAULT NOW(),
    level             VARCHAR(25) NOT NULL,
    message           TEXT        NOT NULL,
    error_message     VARCHAR(2000),
    error_class       VARCHAR(1000),
    error_stack_trace TEXT
);


/**
  Represents a cron job
 */
CREATE TABLE IF NOT EXISTS doddle_cron_job
(
    id          VARCHAR(36)     NOT NULL PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    description VARCHAR(1000),
    expression  VARCHAR(100)    NOT NULL,
    next_run_at TIMESTAMP,
    handler     VARCHAR(128)    NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    enabled     BOOLEAN         NOT NULL DEFAULT TRUE,
    timeout     BIGINT          NOT NULL,
    max_retries                 INTEGER NOT NULL,
    queue_id                    VARCHAR(36) NOT NULL REFERENCES doddle_queue (id) ON DELETE CASCADE
);
