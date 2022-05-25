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
package dev.doddle.core.engine.time;

import dev.doddle.core.engine.time.ticker.TickerStrategy;
import dev.doddle.core.engine.time.ticker.strategies.SystemTickerStrategy;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static java.time.ZoneId.systemDefault;
import static dev.doddle.core.support.Objects.requireNonNull;

public class Clock {

    private final TickerStrategy tickerStrategy;

    /**
     * Create a new clock
     *
     * @param tickerStrategy the ticker strategy to use
     */
    public Clock(final TickerStrategy tickerStrategy) {
        this.tickerStrategy = requireNonNull(tickerStrategy, "tickerStrategy cannot be null");
    }

    /**
     * Create a new clock
     */
    public Clock() {
        this(new SystemTickerStrategy());
    }

    public Clock advance(final long milliseconds) {
        this.tickerStrategy.advance(milliseconds);
        return this;
    }

    public long delta(final long milliseconds) {
        return milliseconds - this.tickerStrategy.millis();
    }

    public long elapsed(final long startTime) {
        return this.tickerStrategy.millis() - startTime;
    }

    public boolean isAfter(final long milliseconds) {
        return milliseconds > this.tickerStrategy.millis();
    }

    public boolean isAfter(final LocalDateTime date) {
        final ZonedDateTime zdt = ZonedDateTime.of(date, systemDefault());
        return this.tickerStrategy.millis() > zdt.toInstant().toEpochMilli();
    }

    public boolean isBefore(final long milliseconds) {
        return milliseconds < this.tickerStrategy.millis();
    }

    public long millis() {
        return this.tickerStrategy.millis();
    }

}
