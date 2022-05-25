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
package dev.doddle.core.engine.progress;

import dev.doddle.core.exceptions.TaskExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobProgressTest {

    @DisplayName("it should calculate the percentage")
    @Test
    void it_should_calculate_the_percentage() {
        final JobProgress progress = new JobProgress(200);
        progress.advance(50);
        assertEquals(25, progress.percentage());
        progress.reset();
        progress.advance(25);
        assertEquals(12, progress.percentage());
    }

    @DisplayName("it should successfully test the consumer")
    @Test
    void it_should_successfully_test_the_consumer() {
        final JobProgress progress = new JobProgress(200, message -> {
            assertEquals(200, message.getMaxValue());
            assertEquals(50, message.getCurrentValue());
            assertEquals(25, message.getPercentage());
            assertEquals("50/200 (25%)", message.toString());

        });
        progress.advance(50);
    }

    @DisplayName("it should successfully test the progress")
    @Test
    void it_should_successfully_test_the_progress() {
        final JobProgress progress = new JobProgress(100);
        assertEquals(100, progress.maxValue());
        progress.advance();
        assertEquals(1, progress.currentValue());
        progress.advance(20);
        assertEquals(21, progress.currentValue());
        progress.reset();
        assertEquals(0, progress.currentValue());
        progress.advance(100);
        assertEquals(100, progress.percentage());
        progress.reset();

        for (int i = 0; i < 100; i++) {
            progress.advance();
        }
        assertEquals(100, progress.currentValue());
    }

    @DisplayName("it should successfully test toString")
    @Test
    void it_should_successfully_test_toString() {
        final JobProgress progress = new JobProgress(200);
        progress.advance(50);
        assertEquals("50/200 (25%)", progress.toString());
    }

    @DisplayName("it should throw an exception because it has been initialised with zero")
    @Test
    void it_should_throw_an_exception_because_it_has_been_initialised_with_zero() {
        final TaskExecutionException exception1 = assertThrows(TaskExecutionException.class, () -> new JobProgress(0));
        assertEquals("progress needs to be initialised with a value greater than zero", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because of advancing by amount that exceeds max value")
    @Test
    void it_should_throw_an_exception_because_of_advancing_by_amount_that_exceeds_max_value() {
        final JobProgress progress = new JobProgress(100);
        final TaskExecutionException exception1 = assertThrows(TaskExecutionException.class, () -> progress.advance(200));
        assertEquals("advancing progress by amount that exceeds the maximum value", exception1.getMessage());
    }

    @DisplayName("it should throw an exception because of advancing by amount that is less than zero")
    @Test
    void it_should_throw_an_exception_because_of_advancing_by_amount_that_is_less_than_zero() {
        final JobProgress progress = new JobProgress(100);
        final TaskExecutionException exception1 = assertThrows(TaskExecutionException.class, () -> progress.advance(-100));
        assertEquals("value must be greater than or equal to zero", exception1.getMessage());
    }

}
