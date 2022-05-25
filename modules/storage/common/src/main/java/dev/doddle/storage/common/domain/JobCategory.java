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
 * Represents a job category in the system
 */
public enum JobCategory {

    /**
     * A normal job represents a job that was not enqueued from a cron
     */
    STANDARD("standard"),

    /**
     * A scheduled job represents a job that was enqueued from a cron
     */
    SCHEDULED("scheduled");

    private final String name;

    JobCategory(final String name) {
        this.name = name;
    }

    /**
     * Get the job category from the given name
     *
     * @param name the given name
     * @return the job category if found, otherwise null
     */
    public static JobCategory fromName(final String name) {
        for (final JobCategory category : JobCategory.values()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    /**
     * Get the name for this category
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }
}
