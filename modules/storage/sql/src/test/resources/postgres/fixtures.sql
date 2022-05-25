-- noinspection SqlResolveForFile
    INSERT INTO doddle_queue (id, name, priority, locked_at)
    VALUES ('bee2f9aa-b62a-11eb-8529-0242ac130003', 'high', 0.00, null);

    INSERT INTO doddle_queue (id, name, priority, locked_at)
    VALUES ('b9c511b4-b62b-11eb-8529-0242ac130003', 'medium', 0.50, null);

    INSERT INTO doddle_queue (id, name, priority, locked_at)
    VALUES ('b9c5133a-b62b-11eb-8529-0242ac130003', 'low', 1.00, now());

INSERT INTO doddle_job (id,
                        name,
                        identifier,
                        queue_id,
                        category,
                        handler,
                        payload,
                        state,
                        created_at,
                        completed_at,
                        scheduled_at,
                        discarded_at,
                        failed_at,
                        max_retries,
                        retries,
                        timeout,
                        tags,
                        progress_max_value,
                        progress_current_value)
VALUES ('1f4c7f90-7cc6-11ea-bc55-0242ac130003',
        'email.registration',
        'email.123',
        'bee2f9aa-b62a-11eb-8529-0242ac130003',
        'standard',
        'mailer.send',
        '{ "message": "Hello world" }',
        'scheduled',
        now(),
        null,
        now(),
        null,
        null,
        5,
        1,
        30000,
        ARRAY ['mailer', 'users'],
        100,
        5);

INSERT INTO doddle_job (id,
                        name,
                        queue_id,
                        category,
                        handler,
                        payload,
                        state,
                        created_at,
                        completed_at,
                        executing_at,
                        scheduled_at,
                        discarded_at,
                        failed_at,
                        max_retries,
                        retries,
                        timeout,
                        tags)
VALUES ('b35eda92-231b-4ea7-ab26-91e1d9759582',
        'email.registration',
        'bee2f9aa-b62a-11eb-8529-0242ac130003',
        'standard',
        'mailer.send',
        '{ "message": "Hello world" }',
        'completed',
        now(),
        now() - interval  ' 1 minute',
        now(),
        now(),
        null,
        null,
        5,
        1,
        30000,
        ARRAY ['mailer', 'groups']);


INSERT INTO doddle_job (id,
                        queue_id,
                        name,
                        category,
                        handler,
                        payload,
                        state,
                        created_at,
                        completed_at,
                        scheduled_at,
                        discarded_at,
                        failed_at,
                        max_retries,
                        retries,
                        timeout,
                        tags)
VALUES ('e50ac54b-879b-454d-9079-81f3038cec26',
        'bee2f9aa-b62a-11eb-8529-0242ac130003',
        'email.registration',
        'standard',
        'mailer.send',
        '{ "message": "Hello world" }',
        'scheduled',
        now(),
        null,
        now() + interval '1 day',
        null,
        null,
        5,
        1,
        30000,
        ARRAY ['mailer']);

INSERT INTO doddle_job (id,
                        name,
                        queue_id,
                        category,
                        handler,
                        payload,
                        state,
                        created_at,
                        completed_at,
                        scheduled_at,
                        discarded_at,
                        failed_at,
                        max_retries,
                        retries,
                        timeout,
                        tags)
VALUES ('18cf1f10-63b4-4eec-a1c5-cdcaba624b22',
        'email.forgot-password',
        'b9c5133a-b62b-11eb-8529-0242ac130003',
        'standard',
        'mailer.send',
        '{ "message": "Hello world" }',
        'available',
        now(),
        null,
        now(),
        null,
        null,
        5,
        1,
        30000,
        ARRAY ['mailer', 'groups']);

INSERT INTO doddle_job_message (id, job_id, level, message)
VALUES ('4a0600b4-75b0-423a-9cdb-fba95fe4ccd4', '1f4c7f90-7cc6-11ea-bc55-0242ac130003', 'INFO',
        'This is a log message');

INSERT INTO doddle_job_message (id, job_id, level, message)
VALUES ('41671551-f67d-4b13-956d-ec3974f1e7f4', '1f4c7f90-7cc6-11ea-bc55-0242ac130003', 'INFO',
        'This is another log message');

INSERT INTO doddle_job_message (id, job_id, level, message)
VALUES ('7edc45c5-7d45-4c12-a30e-aea88de18667', '1f4c7f90-7cc6-11ea-bc55-0242ac130003', 'ERROR',
        'This is an error message');

INSERT INTO doddle_job_message (id, job_id, level, message, error_message, error_class, error_stack_trace)
VALUES ('8c0dd42c-9aa6-4a9d-82cf-3d37737e8832',
        '1f4c7f90-7cc6-11ea-bc55-0242ac130003',
        'INFO',
        'This is an error message with a stack trace',
        'Oh no! Something went wrong',
        'com.example.Processor',
        'A very long verbose stack trace...');

INSERT INTO doddle_job_message (id, job_id, level, message)
VALUES ('be46e954-3057-42a0-8ff4-3b798bceebb4', 'b35eda92-231b-4ea7-ab26-91e1d9759582', 'INFO',
        'This is a log message');

INSERT INTO doddle_cron_job (id, name, description, expression, next_run_at, handler, created_at, enabled, timeout, max_retries, queue_id)
VALUES ('4d075677-174a-4485-ab85-d02ea94bb4dd', 'job1', 'job 1 description', '* * * * *', '2021-02-01 13:52:00',
        'mailer.send', '2021-01-01 00:00:00', true, 60000, 10, 'bee2f9aa-b62a-11eb-8529-0242ac130003');

INSERT INTO doddle_cron_job (id, name, description, expression, next_run_at, handler, created_at, enabled, timeout, max_retries, queue_id)
VALUES ('9a99870d-5ba5-4792-a344-92658a68e5f7', 'job2', 'job 2 description', '0 * * * *', '2021-02-01 13:52:00',
        'mailer.send', '2021-01-01 00:00:00', true, 60000, 15, 'bee2f9aa-b62a-11eb-8529-0242ac130003');

INSERT INTO doddle_cron_job (id, name, description, expression, next_run_at, handler, created_at, enabled, timeout, max_retries, queue_id)
VALUES ('9de9017b-ccec-4cea-bbcb-64735efafc25', 'job3', 'job 3 description', '0 0 * * *', '2021-02-01 13:52:00',
        'mailer.send', '2021-01-01 00:00:00', true, 60000, 20, 'bee2f9aa-b62a-11eb-8529-0242ac130003');


INSERT INTO doddle_job_tag (id, label) VALUES ('1', 'email');
INSERT INTO doddle_job_tag (id, label) VALUES ('2', 'infrastructure');
INSERT INTO doddle_job_tag (id, label) VALUES ('3', 'users');
