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

import dev.doddle.core.engine.JobEnvironment;
import dev.doddle.core.engine.circuitbreaker.CircuitBreakerConfiguration;
import dev.doddle.core.engine.crypto.EncryptionConfiguration;
import dev.doddle.core.engine.logger.JobLoggerConfiguration;
import dev.doddle.core.engine.middleware.MiddlewareConfiguration;
import dev.doddle.core.engine.polling.PollingConfiguration;
import dev.doddle.core.engine.retry.Retryer;
import dev.doddle.core.engine.scheduling.SchedulingConfiguration;
import dev.doddle.core.engine.task.TaskDependencyResolver;
import dev.doddle.core.engine.task.TaskOptions;
import dev.doddle.core.engine.telemetry.TelemetrySubscriber;
import dev.doddle.core.engine.threadnaming.ThreadNamingStrategy;
import dev.doddle.core.engine.time.ticker.TickerStrategy;
import dev.doddle.storage.common.Storage;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class DoddleConfiguration {

    private Storage                     storage;
    private String                      basePackages;
    private ThreadNamingStrategy        threadNamingStrategy;
    private TickerStrategy              tickerStrategy;
    private TaskDependencyResolver      taskDependencyResolver;
    private List<Retryer>               retryers;
    private TaskOptions                 defaultTaskOptions;
    private CircuitBreakerConfiguration circuitBreakerConfiguration;
    private JobEnvironment              environment;
    private JobLoggerConfiguration      loggerConfiguration;
    private PollingConfiguration        pollingConfiguration;
    private SchedulingConfiguration     schedulingConfiguration;
    private EncryptionConfiguration     encryptionConfiguration;
    private List<TelemetrySubscriber>   telemetrySubscribers;
    private MiddlewareConfiguration     middlewareConfiguration;
    private String                      deletionPeriod;

    public String getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String basePackages) {
        this.basePackages = basePackages;
    }

    public CircuitBreakerConfiguration getCircuitBreakerConfiguration() {
        return circuitBreakerConfiguration;
    }

    public void setCircuitBreakerConfiguration(CircuitBreakerConfiguration circuitBreakerConfiguration) {
        this.circuitBreakerConfiguration = circuitBreakerConfiguration;
    }

    public TaskOptions getDefaultTaskOptions() {
        return defaultTaskOptions;
    }

    public void setDefaultTaskOptions(TaskOptions defaultTaskOptions) {
        this.defaultTaskOptions = defaultTaskOptions;
    }

    public String getDeletionPeriod() {
        return deletionPeriod;
    }

    public void setDeletionPeriod(String deletionPeriod) {
        this.deletionPeriod = deletionPeriod;
    }

    public EncryptionConfiguration getEncryptionConfiguration() {
        return encryptionConfiguration;
    }

    public void setEncryptionConfiguration(EncryptionConfiguration encryptionConfiguration) {
        this.encryptionConfiguration = encryptionConfiguration;
    }

    public JobEnvironment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(JobEnvironment environment) {
        this.environment = environment;
    }

    public JobLoggerConfiguration getLoggerConfiguration() {
        return loggerConfiguration;
    }

    public void setLoggerConfiguration(JobLoggerConfiguration loggerConfiguration) {
        this.loggerConfiguration = loggerConfiguration;
    }

    public MiddlewareConfiguration getMiddlewareConfiguration() {
        return middlewareConfiguration;
    }

    public void setMiddlewareConfiguration(MiddlewareConfiguration middlewareConfiguration) {
        this.middlewareConfiguration = middlewareConfiguration;
    }

    public PollingConfiguration getPollingConfiguration() {
        return pollingConfiguration;
    }

    public void setPollingConfiguration(PollingConfiguration pollingConfiguration) {
        this.pollingConfiguration = pollingConfiguration;
    }

    public List<Retryer> getRetryers() {
        return retryers;
    }

    public void setRetryers(List<Retryer> retryers) {
        this.retryers = retryers;
    }

    public SchedulingConfiguration getSchedulingConfiguration() {
        return schedulingConfiguration;
    }

    public void setSchedulingConfiguration(SchedulingConfiguration schedulingConfiguration) {
        this.schedulingConfiguration = schedulingConfiguration;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public TaskDependencyResolver getTaskDependencyResolver() {
        return taskDependencyResolver;
    }

    public void setTaskDependencyResolver(TaskDependencyResolver taskDependencyResolver) {
        this.taskDependencyResolver = taskDependencyResolver;
    }

    public List<TelemetrySubscriber> getTelemetrySubscribers() {
        return telemetrySubscribers;
    }

    public void setTelemetrySubscribers(List<TelemetrySubscriber> telemetrySubscribers) {
        this.telemetrySubscribers = telemetrySubscribers;
    }

    public ThreadNamingStrategy getThreadNamingStrategy() {
        return threadNamingStrategy;
    }

    public void setThreadNamingStrategy(ThreadNamingStrategy threadNamingStrategy) {
        this.threadNamingStrategy = threadNamingStrategy;
    }

    public TickerStrategy getTickerStrategy() {
        return tickerStrategy;
    }

    public void setTickerStrategy(TickerStrategy milliTimesStrategy) {
        this.tickerStrategy = milliTimesStrategy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("pollingConfiguration", pollingConfiguration)
            .append("schedulingConfiguration", schedulingConfiguration)
            .append("storage", storage)
            .append("basePackages", basePackages)
            .append("threadNamingStrategy", threadNamingStrategy)
            .append("tickerStrategy", tickerStrategy)
            .append("taskDependencyResolver", taskDependencyResolver)
            .append("retryers", retryers)
            .append("defaultTaskOptions", defaultTaskOptions)
            .append("circuitBreakerConfiguration", circuitBreakerConfiguration)
            .append("environment", environment)
            .append("loggerConfiguration", loggerConfiguration)
            .append("telemetrySubscribers", telemetrySubscribers)
            .append("encryptionConfiguration", encryptionConfiguration)
            .append("middleware", middlewareConfiguration)
            .append("deletionPeriod", deletionPeriod)
            .toString();
    }
}
