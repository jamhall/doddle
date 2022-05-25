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
package dev.doddle.core;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.engine.JobEnvironment;
import dev.doddle.core.engine.circuitbreaker.CircuitBreakerConfiguration;
import dev.doddle.core.engine.crypto.EncryptionConfiguration;
import dev.doddle.core.engine.logger.JobLoggerConfiguration;
import dev.doddle.core.engine.middleware.Middleware;
import dev.doddle.core.engine.middleware.MiddlewareConfiguration;
import dev.doddle.core.engine.polling.PollingConfiguration;
import dev.doddle.core.engine.retry.RetryStrategy;
import dev.doddle.core.engine.retry.Retryer;
import dev.doddle.core.engine.retry.strategies.ConstantRetryStrategy;
import dev.doddle.core.engine.retry.strategies.JitterRetryStrategy;
import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import dev.doddle.core.engine.retry.strategies.SquaredRetryStrategy;
import dev.doddle.core.engine.scheduling.SchedulingConfiguration;
import dev.doddle.core.engine.task.TaskDependencyResolver;
import dev.doddle.core.engine.task.TaskOptions;
import dev.doddle.core.engine.telemetry.TelemetrySubscriber;
import dev.doddle.core.engine.threadnaming.ThreadNamingStrategy;
import dev.doddle.core.engine.threadnaming.strategies.DefaultThreadNamingStrategy;
import dev.doddle.core.engine.time.ticker.TickerStrategy;
import dev.doddle.core.engine.time.ticker.strategies.SystemTickerStrategy;
import dev.doddle.core.exceptions.DoddleValidationException;
import dev.doddle.storage.common.Storage;
import dev.doddle.storage.common.StorageProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static dev.doddle.core.engine.logger.JobLoggerLevel.INFO;
import static dev.doddle.core.engine.task.TaskOptions.createDefaultTaskOptions;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static dev.doddle.core.support.Objects.requireNonNull;

public class DoddleConfigurationBuilder {

    private final Map<String, Retryer>        retryers;
    private final TaskOptions                 defaultTaskOptions;
    private final MiddlewareConfiguration     middlewareConfiguration;
    private       ThreadNamingStrategy        threadNamingStrategy;
    private       TickerStrategy              tickerStrategy;
    private       TaskDependencyResolver      taskDependencyResolver;
    private       PollingConfiguration        pollingConfiguration;
    private       SchedulingConfiguration     schedulingConfiguration;
    private       Storage                     storage;
    private       CircuitBreakerConfiguration circuitBreakerConfiguration;
    private       String                      basePackages;
    private       JobEnvironment              environment;
    private       JobLoggerConfiguration      loggerConfiguration;
    private       List<TelemetrySubscriber>   telemetrySubscribers;
    private       EncryptionConfiguration     encryptionConfiguration;
    private       String                      deletionPeriod;

    /**
     * Create a new client factory
     */
    public DoddleConfigurationBuilder() {
        this.threadNamingStrategy = new DefaultThreadNamingStrategy();
        this.tickerStrategy = new SystemTickerStrategy();
        this.retryers = new HashMap<>() {{
            put("constant", new Retryer("constant", new ConstantRetryStrategy()));
            put("jitter", new Retryer("jitter", new JitterRetryStrategy()));
            put("linear", new Retryer("linear", new LinearRetryStrategy()));
            put("squared", new Retryer("squared", new SquaredRetryStrategy()));
        }};
        this.environment = new JobEnvironment();
        this.taskDependencyResolver = new TaskDependencyResolver() {
            @Override
            public <T> T resolve(Class<T> type) {
                throw new RuntimeException(format("Cannot resolve class: %s", type.getSimpleName()));
            }
        };
        this.defaultTaskOptions = createDefaultTaskOptions();
        this.basePackages = null;
        this.storage = null;
        this.telemetrySubscribers = new ArrayList<>();
        this.circuitBreakerConfiguration = null;
        this.encryptionConfiguration = null;
        this.loggerConfiguration = new JobLoggerConfiguration(INFO, 10);
        this.middlewareConfiguration = new MiddlewareConfiguration();
        this.encryptionConfiguration = new EncryptionConfiguration(false);
        this.deletionPeriod = null;
    }

    /**
     * Build the configuration for this client
     *
     * @return the built configuration
     */
    public DoddleConfiguration build() {
        final DoddleConfiguration configuration = new DoddleConfiguration();
        configuration.setSchedulingConfiguration(schedulingConfiguration);
        configuration.setThreadNamingStrategy(threadNamingStrategy);
        configuration.setTickerStrategy(tickerStrategy);
        configuration.setStorage(storage);
        configuration.setTaskDependencyResolver(taskDependencyResolver);
        configuration.setDefaultTaskOptions(defaultTaskOptions);
        configuration.setRetryers(new ArrayList<>(retryers.values()));
        configuration.setCircuitBreakerConfiguration(circuitBreakerConfiguration);
        configuration.setBasePackages(basePackages);
        configuration.setEnvironment(environment);
        configuration.setLoggerConfiguration(loggerConfiguration);
        configuration.setPollingConfiguration(pollingConfiguration);
        configuration.setTelemetrySubscribers(telemetrySubscribers);
        configuration.setEncryptionConfiguration(encryptionConfiguration);
        configuration.setMiddlewareConfiguration(middlewareConfiguration);
        configuration.setDeletionPeriod(deletionPeriod);
        return configuration;
    }

    /**
     * Set the circuit breaker configuration
     *
     * @param configuration the circuit breaker configuration
     * @return this
     */
    public DoddleConfigurationBuilder circuitBreaker(@NotNull CircuitBreakerConfiguration configuration) {
        this.circuitBreakerConfiguration = requireNonNull(configuration, "configuration cannot be null");
        return this;
    }

    public DoddleConfigurationBuilder circuitBreaker(@NotNull Function<CircuitBreakerConfiguration, CircuitBreakerConfiguration> configuration) {
        requireNonNull(configuration, "circuit breaker configuration cannot be null");
        this.circuitBreakerConfiguration = configuration.apply(new CircuitBreakerConfiguration());
        return this;
    }

    /**
     * Delete all jobs older than the given period
     *
     * @param period the period
     * @return this
     */
    public DoddleConfigurationBuilder delete(final String period) {
        this.deletionPeriod = requireNonNull(period, "period cannot be null");
        return this;
    }

    public DoddleConfigurationBuilder encryption(@NotNull Function<EncryptionConfiguration, EncryptionConfiguration> configuration) {
        requireNonNull(configuration, "encryption configuration cannot be null");
        this.encryptionConfiguration = configuration.apply(new EncryptionConfiguration(true));
        return this;
    }

    /**
     * Set the environment
     *
     * @param environment the environment
     * @return this
     */
    public DoddleConfigurationBuilder environment(@NotNull Function<JobEnvironment, JobEnvironment> environment) {
        requireNonNull(environment, "environment cannot be null");
        this.environment = environment.apply(new JobEnvironment());
        return this;
    }

    /**
     * Set the logger options for job execution context
     *
     * @param configuration the logger configuration
     * @return this
     */
    public DoddleConfigurationBuilder logger(@NotNull Function<JobLoggerConfiguration, JobLoggerConfiguration> configuration) {
        requireNonNull(configuration, "logger configuration cannot be null");
        this.loggerConfiguration = configuration.apply(new JobLoggerConfiguration());
        return this;
    }

    /**
     * Set the default number of retries for a job
     *
     * @param maxRetries the max number of retries
     * @return this
     */
    public DoddleConfigurationBuilder maxRetries(@NotNull Integer maxRetries) {
        this.defaultTaskOptions.maxRetries(maxRetries);
        return this;
    }

    public DoddleConfigurationBuilder middleware(@NotNull final Middleware... middleware) {
        for (Middleware subscriber : requireNonNull(middleware, "middleware cannot be null")) {
            this.middlewareConfiguration.add(subscriber);
        }
        return this;
    }

    /**
     * Set the path to the packages where the tasks are defined
     *
     * @param path the path to scan
     * @return this
     */
    public DoddleConfigurationBuilder packages(@NotNull String path) {
        this.basePackages = requireNonNull(path, "path cannot be null");
        return this;
    }

    /**
     * Set the polling configuration
     *
     * @param configuration the polling configuration
     * @return this
     */
    public DoddleConfigurationBuilder polling(@NotNull Function<PollingConfiguration, PollingConfiguration> configuration) {
        requireNonNull(configuration, "configuration cannot be null");
        this.pollingConfiguration = configuration.apply(new PollingConfiguration());
        return this;
    }

    /**
     * Register a retry strategy
     *
     * @param name          the name for the retry strategy
     * @param retryStrategy the retry strategy to register
     * @return this
     */
    public DoddleConfigurationBuilder registerRetryStrategy(@NotNull final String name, @NotNull RetryStrategy retryStrategy) {
        requireNonNull(retryStrategy, "retryStrategy cannot be null");
        if (retryers.containsKey(name)) {
            throw new DoddleValidationException("retryStrategy has already been registered");
        } else {
            this.retryers.put(name, new Retryer(name, retryStrategy));
        }
        return this;
    }

    /**
     * Set a resolver. This resolver is used to resolve dependencies when executing the job
     *
     * @param resolver the task dependency resolver to use
     * @return this
     */
    public DoddleConfigurationBuilder resolver(@NotNull TaskDependencyResolver resolver) {
        this.taskDependencyResolver = requireNonNull(resolver, "resolver cannot be null");
        return this;
    }

    /**
     * Set the default retry strategy for jobs
     * If the timeout is exceeded then the job will fail
     *
     * @param retryStrategy the default retry strategy
     * @return this
     */
    public DoddleConfigurationBuilder retryStrategy(@NotNull String retryStrategy) {
        if (retryers.containsKey(retryStrategy)) {
            this.defaultTaskOptions.retryStrategy(retryStrategy);
            return this;
        }
        throw new DoddleValidationException("retryStrategy not found. Register it first before calling this method");
    }

    /**
     * Set the scheduling configuration
     *
     * @param configuration the scheduling configuration
     * @return this
     */
    public DoddleConfigurationBuilder scheduling(@NotNull Function<SchedulingConfiguration, SchedulingConfiguration> configuration) {
        requireNonNull(configuration, "configuration cannot be null");
        this.schedulingConfiguration = configuration.apply(new SchedulingConfiguration());
        return this;
    }

    /**
     * The storage to use for persistence
     *
     * @param storage the storage adapter to use
     * @return this
     */
    public DoddleConfigurationBuilder storage(@NotNull StorageProvider storage) {
        this.storage = new Storage(requireNonNull(storage, "storage cannot be null"));
        return this;
    }

    public DoddleConfigurationBuilder telemetry(@NotNull final TelemetrySubscriber... subscribers) {
        this.telemetrySubscribers = asList(requireNonNull(subscribers, "subscribers cannot be null"));
        return this;
    }

    /**
     * The strategy to use for naming a thread
     *
     * @param threadNamingStrategy the strategy to use
     * @return this
     */
    public DoddleConfigurationBuilder threadNamingStrategy(@NotNull ThreadNamingStrategy threadNamingStrategy) {
        this.threadNamingStrategy = requireNonNull(threadNamingStrategy, "threadNamingStrategy cannot be null");
        return this;
    }

    /**
     * The ticker strategy for getting the time in nanoseconds
     *
     * @param tickerStrategy the ticker to use
     * @return this
     */
    public DoddleConfigurationBuilder ticker(@NotNull TickerStrategy tickerStrategy) {
        this.tickerStrategy = requireNonNull(tickerStrategy, "tickerStrategy cannot be null");
        return this;
    }

    /**
     * Set the default timeout for jobs
     * If the timeout is exceeded then the job will fail
     *
     * @param timeout the default timeout
     * @return this
     */
    public DoddleConfigurationBuilder timeout(@NotNull String timeout) {
        this.defaultTaskOptions.timeout(timeout);
        return this;
    }

    /**
     * Unregister a retry strategy
     *
     * @param name the retryer to remove
     * @return this
     */
    public DoddleConfigurationBuilder unregisterRetryStrategy(@NotNull final String name) {
        if (retryers.containsKey(requireNonNull(name, "name cannot be null"))) {
            this.retryers.remove(name);
        }
        return this;
    }

    /**
     * Returns the number of processors available to the Java virtual machine.
     *
     * @return the maximum number of processors available to the virtual
     * machine; never smaller than one
     */
    private int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

}
