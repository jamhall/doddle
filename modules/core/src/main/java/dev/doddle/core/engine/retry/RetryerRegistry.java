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
package dev.doddle.core.engine.retry;

import dev.doddle.common.support.NotNull;
import dev.doddle.core.exceptions.DoddleValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dev.doddle.core.support.Objects.requireNonNull;

public class RetryerRegistry {

    private static final Logger        logger = LoggerFactory.getLogger(RetryerRegistry.class);
    private final        List<Retryer> retries;
    private final        String        defaultStrategy;

    /**
     * Create a new retry strategy registry that holds a reference to the retry strategies
     */
    public RetryerRegistry(@NotNull List<Retryer> retries,
                           @NotNull String defaultStrategy) {
        if (requireNonNull(retries, "retries cannot be null").size() == 0) {
            throw new DoddleValidationException("there must be at least one retry registered");
        }
        this.retries = retries;
        this.defaultStrategy = requireNonNull(defaultStrategy, "defaultStrategy cannot be null");
        logger.debug("Registered {} retry strategies", retries.size());
    }

    /**
     * Get the number of registered strategies
     *
     * @return the number of strategies
     */
    public int countAll() {
        return retries.size();
    }

    /**
     * Get all of the registered strategies
     *
     * @return a list of retries
     */
    public List<Retryer> getAll() {
        return this.retries;
    }

    /**
     * Get the default retry strategy
     *
     * @return the default strategy
     */
    public Retryer getDefault() {
        return this.getForName(defaultStrategy);
    }

    /**
     * Get a queue for a given name
     *
     * @param name the strategy to find
     * @return the strategy otherwise null
     */
    public Retryer getForName(@NotNull final String name) {
        return this.retries.stream()
            .filter(retry -> retry.getName().equals(
                requireNonNull(name, "name cannot be null"))
            )
            .findAny()
            .orElse(null);

    }

    /**
     * Check if a strategy is registered
     *
     * @param name the strategy to check if it is registered
     * @return true if registered, otherwise null
     */
    public boolean isRegistered(@NotNull final String name) {
        return getForName(name) != null;
    }
}
