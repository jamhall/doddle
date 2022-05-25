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

import dev.doddle.core.engine.logger.JobLoggerLevel;
import dev.doddle.core.engine.mapper.NoopMapperAdapter;
import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import dev.doddle.core.engine.retry.strategies.NoopRetryStrategy;
import dev.doddle.core.engine.task.NoopTaskDependencyResolver;
import dev.doddle.core.engine.threadnaming.strategies.NoopThreadNamingStrategy;
import dev.doddle.core.engine.time.ticker.strategies.FakeTickerStrategy;
import dev.doddle.core.exceptions.DoddleValidationException;
import dev.doddle.storage.common.NoopStorageProvider;
import dev.doddle.storage.common.Storage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dev.doddle.core.engine.time.Interval.createInterval;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

class DoddleConfigurationBuilderTest {

    @DisplayName("it should successfully build a configuration")
    @Test
    public void it_should_successfully_build_a_configuration() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        builder
            .scheduling(options -> {
                options.interval("15s");
                return options;
            })
            .ticker(new FakeTickerStrategy())
            .registerRetryStrategy("noop", new NoopRetryStrategy())
            .unregisterRetryStrategy("linear")
            .packages("dev.doddle.core")
            .circuitBreaker(cb -> {
                cb.ticker(new FakeTickerStrategy())
                    .maxSuccessCount(10)
                    .maxFailureCount(15);
                return cb;
            })
            .polling(polling -> {
                polling.concurrency(5)
                    .interval("100ms");
                return polling;
            })
            .logger(logger -> {
                logger.level(JobLoggerLevel.DEBUG)
                    .maxLines(100);
                return logger;
            })
            .environment(environment -> {
                environment.put("test", true);
                return environment;
            })
            // make this fluent
            .maxRetries(100)
            .timeout("100ms")
            .threadNamingStrategy(new NoopThreadNamingStrategy())
            .resolver(new NoopTaskDependencyResolver())
            .storage(new NoopStorageProvider());
        DoddleConfiguration configuration = builder.build();
        assertEquals(5, configuration.getPollingConfiguration().concurrency());
        assertEquals(createInterval(100, MILLISECONDS), configuration.getPollingConfiguration().interval());
        assertEquals(createInterval(15, SECONDS), configuration.getSchedulingConfiguration().interval());
        assertTrue(configuration.getTickerStrategy() instanceof FakeTickerStrategy);
        assertTrue(configuration.getTaskDependencyResolver() instanceof NoopTaskDependencyResolver);
        assertTrue(configuration.getThreadNamingStrategy() instanceof NoopThreadNamingStrategy);
        assertNotNull(configuration.getCircuitBreakerConfiguration());
        assertNotNull(configuration.getLoggerConfiguration());
        assertNotNull(configuration.getEnvironment());
        assertNotNull(configuration.getDefaultTaskOptions());
        assertNotNull(configuration.getStorage());
        assertEquals("dev.doddle.core", configuration.getBasePackages());
        assertEquals(4, configuration.getRetryers().size());
    }

    @DisplayName("it should throw an exception because invalid concurrency")
    @Test
    public void it_should_throw_an_exception_because_invalid_concurrency() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final DoddleValidationException exception1 = assertThrows(DoddleValidationException.class, () -> builder.polling(options -> options.concurrency(-1)));
        assertEquals("Concurrency cannot be less than zero", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because invalid environment")
    @Test
    public void it_should_throw_an_exception_because_invalid_environment() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> builder.environment(null));
        assertEquals("environment cannot be null", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because invalid logger")
    @Test
    public void it_should_throw_an_exception_because_invalid_logger() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> builder.logger(null));
        assertEquals("logger configuration cannot be null", exception1.getMessage());
    }


    @DisplayName("it should throw an exception because invalid packages path")
    @Test
    public void it_should_throw_an_exception_because_invalid_packages_path() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> builder.packages(null));
        assertEquals("path cannot be null", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because invalid polling interval")
    @Test
    public void it_should_throw_an_exception_because_invalid_polling_interval() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final DoddleValidationException exception1 = assertThrows(DoddleValidationException.class, () -> builder.polling(options -> options.interval("-1m")));
        assertEquals("Could not parse given interval", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because invalid retry strategy")
    @Test
    public void it_should_throw_an_exception_because_invalid_retry_strategy() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> builder.registerRetryStrategy(null, null));
        final DoddleValidationException exception2 = assertThrows(DoddleValidationException.class, () -> builder.registerRetryStrategy("linear", new LinearRetryStrategy()));
        final NullPointerException exception3 = assertThrows(NullPointerException.class, () -> builder.unregisterRetryStrategy(null));
        assertEquals("retryStrategy cannot be null", exception1.getMessage());
        assertEquals("retryStrategy has already been registered", exception2.getMessage());
        assertEquals("retryStrategy cannot be null", exception3.getMessage());
    }

    @DisplayName("it should throw an exception because invalid scheduling interval")
    @Test
    public void it_should_throw_an_exception_because_invalid_scheduling_interval() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final DoddleValidationException exception1 = assertThrows(DoddleValidationException.class, () -> builder.scheduling(options -> options.interval("-1m")));
        assertEquals("Could not parse given interval", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because invalid storage")
    @Test
    public void it_should_throw_an_exception_because_invalid_storage() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> builder.storage(null));
        assertEquals("storage cannot be null", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because invalid task dependency resolver")
    @Test
    public void it_should_throw_an_exception_because_invalid_task_dependency_resolver() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> builder.resolver(null));
        assertEquals("resolver cannot be null", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because invalid thread naming strategy")
    @Test
    public void it_should_throw_an_exception_because_invalid_thread_naming_strategy() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> builder.threadNamingStrategy(null));
        assertEquals("threadNamingStrategy cannot be null", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because invalid ticker strategy")
    @Test
    public void it_should_throw_an_exception_because_invalid_ticker_strategy() {
        final DoddleConfigurationBuilder builder = new DoddleConfigurationBuilder();
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> builder.ticker(null));
        assertEquals("tickerStrategy cannot be null", exception1.getMessage());
    }
}
