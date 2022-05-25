-- noinspection SqlResolveForFile @ object-type/"doddle_job_state"

CREATE TABLE IF NOT EXISTS doddle_queue (
    id        VARCHAR(36) NOT NULL PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    priority  NUMERIC (3, 2) NOT NULL,
    locked_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS doddle_job (
    id                     VARCHAR(36)                    UNIQUE NOT NULL PRIMARY KEY,
    queue_id               VARCHAR(36)          		  NOT NULL REFERENCES doddle_queue (id) ON DELETE CASCADE,
    category               ENUM('standard', 'scheduled')  NOT NULL,
    handler                VARCHAR(128)                   NOT NULL,
    payload                TEXT,
    state                  ENUM ('available', 'scheduled','executing', 'retryable', 'completed', 'discarded', 'failed') NOT NULL,
    created_at             TIMESTAMP                      NOT NULL DEFAULT NOW(),
    completed_at           TIMESTAMP,
    scheduled_at           TIMESTAMP                      NOT NULL DEFAULT NOW(),
    discarded_at           TIMESTAMP,
    executing_at           TIMESTAMP,
    failed_at              TIMESTAMP,
    max_retries            INTEGER                        NOT NULL,
    retries                INTEGER                        NOT NULL DEFAULT 0,
    timeout                BIGINT                         NOT NULL,
--     tags                   TEXT[],
    progress_max_value     MEDIUMINT,
    progress_current_value MEDIUMINT,
    error_message          VARCHAR(2000),
    error_throwable        VARCHAR(1000),
    error_stack_trace      TEXT,
    INDEX doddle_job_state_idx (state),
    INDEX doddle_job_category_idx (category),
    INDEX doddle_job_scheduled_at_idx (scheduled_at)
);

CREATE TABLE IF NOT EXISTS doddle_job_message (
    id                VARCHAR(36) UNIQUE NOT NULL PRIMARY KEY,
    job_id            VARCHAR(36) NOT NULL REFERENCES doddle_job (id) ON DELETE CASCADE,
    created_at        TIMESTAMP   NOT NULL DEFAULT NOW(),
    level             VARCHAR(25) NOT NULL,
    message           TEXT        NOT NULL,
    error_message     VARCHAR(2000),
    error_class       VARCHAR(1000),
    error_stack_trace TEXT,
    INDEX doddle_job_message_level_idx (level)
);

CREATE TABLE IF NOT EXISTS doddle_cron_job (
    id          VARCHAR(36)     NOT NULL PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    description VARCHAR(1000),
    expression  VARCHAR(100)    NOT NULL,
    next_run_at TIMESTAMP,
    handler     VARCHAR(128)    NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    enabled     BOOLEAN         NOT NULL DEFAULT TRUE,
    timeout     BIGINT          NOT NULL,
    queue       VARCHAR(100)    NOT NULL
);
