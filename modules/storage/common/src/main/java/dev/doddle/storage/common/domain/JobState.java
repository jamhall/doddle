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
package dev.doddle.storage.common.domain;

/**
 * Represents a job state in the system
 */
public enum JobState {
    /**
     * A job is available for processing
     */
    AVAILABLE("available"),
    /**
     * A job is scheduled to be processed
     */
    SCHEDULED("scheduled"),
    /**
     * A job is currently being executed
     */
    EXECUTING("executing"),
    /**
     * The job has been marked as retryable and will later be scheduled for processing
     */
    RETRYABLE("retryable"),
    /**
     * The job completely successfully. No more processing will take place.
     */
    COMPLETED("completed"),
    /**
     * The job has been discarded. There are two ways this can happen
     * 1. The job exhausted its number of retries
     * 2. The job was manually discarded by the user
     */
    DISCARDED("discarded"),
    /**
     * The job failed
     */
    FAILED("failed");

    private final String name;

    JobState(final String name) {
        this.name = name;
    }

    /**
     * Get the state for a given name
     *
     * @param name the name to lookup
     * @return the state
     */
    public static JobState fromName(final String name) {
        for (final JobState state : JobState.values()) {
            if (state.getName().equalsIgnoreCase(name)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Get the name for this state
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }
}
