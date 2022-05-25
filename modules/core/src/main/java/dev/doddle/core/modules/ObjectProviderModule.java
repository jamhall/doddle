/*
 * MIT License
 *
 * Copyright (c) 2022 Jamie Hall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.doddle.core.modules;

import dev.doddle.common.support.NotNull;
import dev.doddle.common.support.Provides;
import dev.doddle.common.support.Singleton;
import dev.doddle.core.DoddleClient;
import dev.doddle.core.DoddleConfiguration;
import dev.doddle.core.engine.*;
import dev.doddle.core.engine.circuitbreaker.CircuitBreaker;
import dev.doddle.core.engine.circuitbreaker.CircuitBreakerConfiguration;
import dev.doddle.core.engine.crypto.AesEncryptionAdapter;
import dev.doddle.core.engine.crypto.EncryptionConfiguration;
import dev.doddle.core.engine.crypto.EncryptionService;
import dev.doddle.core.engine.crypto.EncryptionStore;
import dev.doddle.core.engine.logger.JobLoggerConfiguration;
import dev.doddle.core.engine.logger.JobLoggerFactory;
import dev.doddle.core.engine.mapper.JacksonMapperAdapter;
import dev.doddle.core.engine.middleware.MiddlewareConfiguration;
import dev.doddle.core.engine.middleware.MiddlewarePipeline;
import dev.doddle.core.engine.polling.PollingExecutionPool;
import dev.doddle.core.engine.polling.PollingManager;
import dev.doddle.core.engine.polling.loop.DefaultLoopStrategy;
import dev.doddle.core.engine.polling.loop.LoopStrategy;
import dev.doddle.core.engine.polling.loop.PollingLoop;
import dev.doddle.core.engine.retry.Retryer;
import dev.doddle.core.engine.retry.RetryerRegistry;
import dev.doddle.core.engine.scheduling.SchedulingConfiguration;
import dev.doddle.core.engine.scheduling.SchedulingManager;
import dev.doddle.core.engine.scheduling.commands.CronJobCommand;
import dev.doddle.core.engine.scheduling.commands.JanitorCommand;
import dev.doddle.core.engine.scheduling.commands.JobDeleteCommand;
import dev.doddle.core.engine.scheduling.commands.JobEnqueueCommand;
import dev.doddle.core.engine.task.*;
import dev.doddle.core.engine.telemetry.TelemetrySubscriber;
import dev.doddle.core.engine.threadnaming.ThreadNamingStrategy;
import dev.doddle.core.engine.time.Clock;
import dev.doddle.core.engine.time.Interval;
import dev.doddle.core.engine.time.ticker.TickerStrategy;
import dev.doddle.core.services.*;
import dev.doddle.storage.common.Storage;

import java.util.List;

import static dev.doddle.core.support.Objects.requireNonNull;
import static java.util.Arrays.asList;

public class ObjectProviderModule {

    private final DoddleConfiguration configuration;

    /**
     * Create a new object provider
     *
     * @param configuration the client configuration
     */
    public ObjectProviderModule(@NotNull DoddleConfiguration configuration) {
        this.configuration = requireNonNull(configuration, "configuration cannot be null");
    }

    /**
     * Create the circuit breaker
     *
     * @return the circuit breaker
     */
    @Provides
    @Singleton
    public CircuitBreaker createCircuitBreaker() {
        final CircuitBreakerConfiguration configuration = this.configuration.getCircuitBreakerConfiguration();
        return new CircuitBreaker(
            configuration.maxSuccessCount(),
            configuration.maxFailureCount(),
            configuration.retryTimeout(),
            configuration.ticker()
        );
    }

    /**
     * Create a new client
     *
     * @return the new client
     */
    @Provides
    @Singleton
    public DoddleClient createClient(@NotNull final JobService jobService,
                                     @NotNull final CronJobService cronJobService,
                                     @NotNull final QueueService queueService,
                                     @NotNull final TaskService taskService,
                                     @NotNull final PollingManager polling,
                                     @NotNull final SchedulingManager scheduling) {
        return new DoddleClient(
            jobService,
            cronJobService,
            queueService,
            taskService,
            polling,
            scheduling
        );
    }

    @Provides
    @Singleton
    public CronJobCommand createCronJobCommand(@NotNull final Storage storage,
                                               @NotNull final Clock clock,
                                               @NotNull final CronService cronService,
                                               @NotNull final TaskService taskService,
                                               @NotNull final TelemetryService telemetryService,
                                               @NotNull final CircuitBreaker circuitBreaker) {
        return new CronJobCommand(
            storage,
            clock,
            cronService,
            taskService,
            telemetryService,
            circuitBreaker);
    }

    /**
     * Create the cron job service
     *
     * @param taskService      the task service
     * @param storage          the storage
     * @param cronService      the cron service
     * @param telemetryService the telemetry service
     * @return the cron job service
     */
    @Provides
    @Singleton
    public CronJobService createCronJobService(@NotNull final TaskService taskService,
                                               @NotNull final Storage storage,
                                               @NotNull final CronService cronService,
                                               @NotNull final TelemetryService telemetryService) {
        final TaskOptions defaultTaskOptions = configuration.getDefaultTaskOptions();
        return new CronJobService(taskService, storage, cronService, telemetryService, defaultTaskOptions);
    }

    @Provides
    @Singleton
    public CronService createCronService() {
        return new CronService();
    }

    @Provides
    @Singleton
    public EncryptionService createEncryptionService() {
        final EncryptionConfiguration configuration = this.configuration.getEncryptionConfiguration();
        final EncryptionStore store = configuration.getStore();
        final boolean enabled = configuration.isEnabled();
        final AesEncryptionAdapter adapter = new AesEncryptionAdapter();
        return new EncryptionService(enabled, store, adapter);
    }

    @Provides
    @Singleton
    public JanitorCommand createJanitorCommand(@NotNull final Storage storage) {
        return new JanitorCommand(storage);
    }

    /**
     * Create the task data mapper
     *
     * @return the task data mapper
     */
    @Provides
    @Singleton
    public JobDataMapper createJobDataMapper(EncryptionService encryptionService) {
        return new JobDataMapper(new JacksonMapperAdapter(encryptionService));
    }

    @Provides
    @Singleton
    public JobDeleteCommand createJobDeleteCommand(@NotNull final JobService jobService) {
        final String period = this.configuration.getDeletionPeriod();
        return new JobDeleteCommand(jobService, period);
    }

    @Provides
    @Singleton
    public JobEnqueueCommand createJobEnqueueCommand(
        @NotNull final Storage storage,
        @NotNull final TelemetryService telemetryService,
        @NotNull final CircuitBreaker circuitBreaker) {
        return new JobEnqueueCommand(storage, telemetryService, circuitBreaker);
    }

    @Provides
    @Singleton
    public JobEnvironment createJobEnvironment() {
        return this.configuration.getEnvironment();
    }

    @Provides
    @Singleton
    public JobExecutionContextEventBus createJobExecutionContextEventBus(@NotNull final Storage storage) {
        return new JobExecutionContextEventBus(storage);
    }

    @Provides
    @Singleton
    public JobExecutionContextFactory createJobExecutionContextFactory(
        @NotNull final JobExecutionContextEventBus eventBus,
        @NotNull final JobEnvironment environment,
        @NotNull final JobDataMapper mapper) {
        final JobLoggerConfiguration loggerConfiguration = this.configuration.getLoggerConfiguration();
        return new JobExecutionContextFactory(eventBus, environment, mapper, loggerConfiguration);
    }

    @Provides
    @Singleton
    public PollingExecutionPool createJobExecutionPool(@NotNull final JobRunner runner,
                                                       @NotNull final PollingLoop loop) {
        final ThreadNamingStrategy threadNamingStrategy = configuration.getThreadNamingStrategy();
        final Integer concurrency = configuration.getPollingConfiguration().concurrency();
        return new PollingExecutionPool(threadNamingStrategy, concurrency, loop, runner);
    }

    @Provides
    @Singleton
    public JobLoggerFactory createJobLoggerFactory() {
        return new JobLoggerFactory(configuration.getLoggerConfiguration());
    }

    /**
     * Create a job loop
     *
     * @param loopStrategy the loop strategy
     * @return the job loop
     */
    @Provides
    @Singleton
    public PollingLoop createJobLoop(@NotNull LoopStrategy loopStrategy) {
        final Interval pollingInterval = configuration.getPollingConfiguration().interval();
        return new PollingLoop(loopStrategy, pollingInterval);
    }

    /**
     * Create the job picker
     *
     * @param storage          the storage
     * @param clock            the clock
     * @param telemetryService the telemetry service
     * @param circuitBreaker   the circuit breaker
     * @return a new job picker
     */
    @Provides
    @Singleton
    public JobPicker createJobPicker(@NotNull final Storage storage,
                                     @NotNull final Clock clock,
                                     @NotNull final TelemetryService telemetryService,
                                     @NotNull final CircuitBreaker circuitBreaker) {
        return new JobPicker(storage, clock, telemetryService, circuitBreaker);
    }

    /**
     * Create the job processor
     *
     * @return the job processor
     */
    @Provides
    @Singleton
    public JobProcessor createJobProcessor(@NotNull final TaskService taskService,
                                           @NotNull final JobExecutionContextFactory contextFactory,
                                           @NotNull final JobResultProcessor resultProcessor,
                                           @NotNull final TelemetryService telemetryService,
                                           @NotNull final Storage storage,
                                           @NotNull final MiddlewarePipeline middleware) {
        return new JobProcessor(taskService,
            resultProcessor,
            contextFactory,
            telemetryService,
            middleware,
            storage);
    }

    /**
     * Create a job result processor
     *
     * @return a new job result processor
     */
    @Provides
    public JobResultProcessor createJobResultProcessor(@NotNull final JobRetryer retryer, @NotNull final Storage storage) {
        return new JobResultProcessor(retryer, storage);
    }

    @Provides
    @Singleton
    public JobRetryer createJobRetryer() {
        return new JobRetryer();
    }

    /**
     * Create the job runner
     *
     * @param processor the job processor
     * @param picker    the job picker
     * @return the job runner
     */
    @Provides
    @Singleton
    public JobRunner createJobRunner(@NotNull final JobProcessor processor,
                                     @NotNull final JobPicker picker) {
        return new JobRunner(Runnable::run, processor, picker);
    }

    @Provides
    @Singleton
    public JobService createJobService(@NotNull final JobDataMapper mapper,
                                       @NotNull final TaskService taskService,
                                       @NotNull final Storage storage,
                                       @NotNull final TelemetryService telemetryService,
                                       @NotNull final EncryptionService encryptionService) {
        final TaskOptions defaultTaskOptions = configuration.getDefaultTaskOptions();
        return new JobService(
            taskService,
            storage,
            mapper,
            telemetryService,
            encryptionService,
            defaultTaskOptions
        );
    }

    /**
     * Create the job loop strategy
     *
     * @param clock the ticker to use
     * @return the loop strategy
     */
    @Provides
    @Singleton
    public LoopStrategy createLoopStrategy(@NotNull Clock clock) {
        return new DefaultLoopStrategy(clock);
    }

    @Provides
    public MiddlewarePipeline createMiddleware() {
        final MiddlewareConfiguration configuration = this.configuration.getMiddlewareConfiguration();
        return configuration.build();
    }

    /**
     * Create the job scheduler
     *
     * @return the new scheduler
     */
    @Provides
    @Singleton
    public PollingManager createPollingManager(@NotNull final PollingExecutionPool pool) {
        return new PollingManager(pool);
    }

    /**
     * Create the queue service
     *
     * @param storage the storage
     * @return the queue service
     */
    @Provides
    @Singleton
    public QueueService createQueueService(
        @NotNull final Storage storage
    ) {
        return new QueueService(storage);
    }

    @Provides
    @Singleton
    public RetryerRegistry createRetryStrategyRegistry() {
        final String defaultStrategy = configuration.getDefaultTaskOptions().retryStrategy();
        final List<Retryer> strategies = configuration.getRetryers();
        return new RetryerRegistry(strategies, defaultStrategy);
    }

    @Provides
    @Singleton
    public SchedulingManager createSchedulingManager(
        @NotNull final JobEnqueueCommand jobEnqueueCommand,
        @NotNull final CronJobCommand cronJobCommand,
        @NotNull final JanitorCommand janitorCommand,
        @NotNull final JobDeleteCommand jobDeleteCommand) {
        final SchedulingConfiguration configuration = this.configuration.getSchedulingConfiguration();
        return new SchedulingManager(
            configuration.delay(),
            configuration.interval(),
            configuration.threadNaming(),
            asList(cronJobCommand, jobEnqueueCommand, janitorCommand, jobDeleteCommand)
        );
    }

    @Provides
    @Singleton
    public Storage createStorage() {
        return this.configuration.getStorage();
    }

    @Provides
    @Singleton
    public TaskDescriptorFactory createTaskDescriptorFactory(@NotNull final RetryerRegistry retryRegistry) {
        final TaskOptions defaultTaskOptions = configuration.getDefaultTaskOptions();
        return new TaskDescriptorFactory(retryRegistry, defaultTaskOptions);
    }

    /**
     * Create the task executor
     *
     * @return the task executor
     */
    @Provides
    @Singleton
    public TaskExecutor createTaskExecutor() {
        final TaskDependencyResolver resolver = this.configuration.getTaskDependencyResolver();
        return new TaskExecutor(resolver);
    }

    /**
     * Creates the task parser
     *
     * @return the task parser
     */
    @Provides
    @Singleton
    public TaskParser createTaskParser(@NotNull final TaskDescriptorFactory taskDescriptorFactory) {
        return new TaskParser(taskDescriptorFactory);
    }

    /**
     * Create the task registry
     *
     * @return the task registry
     */
    @Provides
    @Singleton
    public TaskRegistry createTaskRegistry(@NotNull final TaskParser parser) {
        final List<TaskDescriptor> descriptors = parser.parse(configuration.getBasePackages());
        return new TaskRegistry(descriptors);
    }

    /**
     * Create the task service
     *
     * @return the task service
     */
    @Provides
    @Singleton
    public TaskService createTaskService(@NotNull final TaskRegistry registry,
                                         @NotNull final TaskExecutor executor) {
        return new TaskService(registry, executor);
    }

    @Provides
    @Singleton
    public TelemetryService createTelemetryService() {
        final List<TelemetrySubscriber> subscribers = this.configuration.getTelemetrySubscribers();
        return new TelemetryService(subscribers);
    }

    /**
     * Create a new ticker
     *
     * @return the new ticker
     */
    @Provides
    @Singleton
    public Clock createTicker() {
        final TickerStrategy tickerStrategy = configuration.getTickerStrategy();
        return new Clock(tickerStrategy);
    }

}
