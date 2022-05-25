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

import dev.doddle.core.engine.time.ticker.strategies.FakeTickerStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.jupiter.api.Assertions.*;

class StopwatchTest {

    private final FakeTickerStrategy ticker    = new FakeTickerStrategy();
    private final Stopwatch          stopwatch = new Stopwatch(new Clock(ticker));

    @DisplayName("it should throw an exception because stopwatch has already been stopped")
    @Test
    public void it_should_stop_when_already_stopped() {
        stopwatch.start();
        stopwatch.stop();
        assertThrows(IllegalStateException.class, stopwatch::stop);
        assertFalse(stopwatch.isRunning());
    }

    @DisplayName("it should test elapsed duration")
    @Test
    public void it_should_test_elapsed_duration() {
        stopwatch.start();
        ticker.advance(999999);
        assertEquals(Duration.ofNanos(999999), stopwatch.elapsed());
        ticker.advance(1);
        assertEquals(Duration.ofMillis(1), stopwatch.elapsed());
    }

    @DisplayName("it should test elapsed microseconds")
    @Test
    public void it_should_test_elapsed_microseconds() {
        stopwatch.start();
        ticker.advance(999);
        assertEquals(0, stopwatch.elapsed(MICROSECONDS));
        ticker.advance(1);
        assertEquals(1, stopwatch.elapsed(MICROSECONDS));
    }

    @DisplayName("it should test elapsed milliseconds")
    @Test
    public void it_should_test_elapsed_milliseconds() {
        stopwatch.start();
        ticker.advance(999999);
        assertEquals(0, stopwatch.elapsed(MILLISECONDS));
        ticker.advance(1);
        assertEquals(1, stopwatch.elapsed(MILLISECONDS));
    }

    @DisplayName("it should test elapsed multiple segments")
    @Test
    public void it_should_test_elapsed_multiple_segments() {
        stopwatch.start();
        ticker.advance(9);
        stopwatch.stop();

        ticker.advance(16);

        stopwatch.start();
        assertEquals(9, stopwatch.elapsed(NANOSECONDS));
        ticker.advance(25);
        assertEquals(34, stopwatch.elapsed(NANOSECONDS));

        stopwatch.stop();
        ticker.advance(36);
        assertEquals(34, stopwatch.elapsed(NANOSECONDS));
    }

    @DisplayName("it should test resetting the ticker")
    @Test
    public void it_should_test_resetting_ticker() {
        ticker.advance(1);
        stopwatch.reset();
        assertFalse(stopwatch.isRunning());
        ticker.advance(2);
        assertEquals(0, stopwatch.elapsed(NANOSECONDS));
        stopwatch.start();
        ticker.advance(3);
        assertEquals(3, stopwatch.elapsed(NANOSECONDS));
    }

    @DisplayName("it should test resetting the ticker while stop watch is running")
    @Test
    public void it_should_test_resetting_ticker_while_stopwatch_is_running() {
        ticker.advance(1);
        stopwatch.start();
        assertEquals(0, stopwatch.elapsed(NANOSECONDS));
        ticker.advance(2);
        assertEquals(2, stopwatch.elapsed(NANOSECONDS));
        stopwatch.reset();
        assertFalse(stopwatch.isRunning());
        ticker.advance(3);
        assertEquals(0, stopwatch.elapsed(NANOSECONDS));
    }

    @DisplayName("it should test start")
    @Test
    public void it_should_test_start() {
        assertSame(stopwatch, stopwatch.start());
        assertTrue(stopwatch.isRunning());
    }

    @DisplayName("it should test start while running")
    @Test
    public void it_should_test_start_while_running() {
        stopwatch.start();
        try {
            stopwatch.start();
            fail();
        } catch (IllegalStateException expected) {
        }
        assertTrue(stopwatch.isRunning());
    }

    @DisplayName("it should test stop")
    @Test
    public void it_should_test_stop() {
        stopwatch.start();
        assertSame(stopwatch, stopwatch.stop());
        assertFalse(stopwatch.isRunning());
    }

    @DisplayName("it should test the elapsed time while stop watch is not running")
    @Test
    public void it_should_test_the_elapsed_time_while_stop_watch_is_not_running() {
        ticker.advance(1);
        stopwatch.start();
        ticker.advance(4);
        stopwatch.stop();
        ticker.advance(9);
        assertEquals(4, stopwatch.elapsed(NANOSECONDS));
    }

    @DisplayName("it should test the elapsed time while stop watch is running")
    @Test
    public void it_should_test_the_elapsed_time_while_stop_watch_is_running() {
        ticker.advance(78);
        stopwatch.start();
        assertEquals(0, stopwatch.elapsed(NANOSECONDS));
        ticker.advance(345);
        assertEquals(345, stopwatch.elapsed(NANOSECONDS));
    }

    @DisplayName("it should test to string")
    @Test
    public void it_should_test_to_string() {
        stopwatch.start();
        assertEquals("0.000 ns", stopwatch.toString());
        ticker.advance(1);
        assertEquals("1.000 ns", stopwatch.toString());
        ticker.advance(998);
        assertEquals("999.0 ns", stopwatch.toString());
        ticker.advance(1);
        assertEquals("1.000 \u03bcs", stopwatch.toString());
        ticker.advance(1);
        assertEquals("1.001 \u03bcs", stopwatch.toString());
        ticker.advance(8998);
        assertEquals("9.999 \u03bcs", stopwatch.toString());
        stopwatch.reset();
        stopwatch.start();
        ticker.advance(1234567);
        assertEquals("1.235 ms", stopwatch.toString());
        stopwatch.reset();
        stopwatch.start();
        ticker.advance(5000000000L);
        assertEquals("5.000 s", stopwatch.toString());
        stopwatch.reset();
        stopwatch.start();
        ticker.advance((long) (1.5 * 60 * 1000000000L));
        assertEquals("1.500 min", stopwatch.toString());
        stopwatch.reset();
        stopwatch.start();
        ticker.advance((long) (2.5 * 60 * 60 * 1000000000L));
        assertEquals("2.500 h", stopwatch.toString());
        stopwatch.reset();
        stopwatch.start();
        ticker.advance((long) (7.25 * 24 * 60 * 60 * 1000000000L));
        assertEquals("7.250 d", stopwatch.toString());
    }

    @DisplayName("it should test initial state")
    @Test
    void it_should_test_initial_state() {
        assertFalse(stopwatch.isRunning());
        assertEquals(0, stopwatch.elapsed(NANOSECONDS));
    }


}
