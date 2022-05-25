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
package dev.doddle.core.engine.logger;

import static java.lang.Integer.MAX_VALUE;

public enum JobLoggerLevel {

    OFF("OFF", MAX_VALUE),
    ERROR("ERROR", 4000),
    INFO("INFO", 3000),
    DEBUG("DEBUG", 2000),
    TRACE("TRACE", 1000);

    private final String label;
    private final int    level;

    JobLoggerLevel(
        final String label,
        final int level) {
        this.label = label;
        this.level = level;
    }

    /**
     * Get the the label for this level
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Check if the the level is loggable from the supplied level
     *
     * @param level the level to check against
     * @return if loggable or not
     */
    public boolean isLoggable(final JobLoggerLevel level) {
        return level.level <= this.level;
    }
}
