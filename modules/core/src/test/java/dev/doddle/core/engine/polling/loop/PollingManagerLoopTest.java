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
package dev.doddle.core.engine.polling.loop;

import dev.doddle.core.engine.JobRunner;
import dev.doddle.core.engine.time.Clock;
import dev.doddle.core.engine.time.Interval;
import dev.doddle.core.engine.time.ticker.strategies.SystemTickerStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(TestExtension.class)
class PollingManagerLoopTest {

    @Test
    @DisplayName("it should test the job loop")
    void it_should_test_the_job_loop() {
        final Clock clock = new Clock(new SystemTickerStrategy());
        final ControlledLoopStrategy loopStrategy = spy(
            new ControlledLoopStrategy(
                new DefaultLoopStrategy(clock),
                5)
        );
        final Interval interval = new Interval(1);
        final PollingLoop loop = new PollingLoop(loopStrategy, interval);
        final JobRunner runner = mock(JobRunner.class);
        loop.start(runner);
        verify(loopStrategy, times(1)).doRun(any());
        verify(runner, times(5)).execute(any());
        assertEquals(5, loopStrategy.getAttemptCount());
    }


    private static class ControlledLoopStrategy implements LoopStrategy {

        private final LoopStrategy delegate;
        private final int          maxAttempts;
        private       int          attemptCount = 0;

        private ControlledLoopStrategy(LoopStrategy delegate, int maxAttempts) {
            this.delegate = delegate;
            this.maxAttempts = maxAttempts;
        }

        @Override
        public void doRun(Runnable runnable) {
            delegate.doRun(() -> {
                if (attemptCount >= maxAttempts) {
                    Thread.currentThread().interrupt();
                    return;
                }
                this.attemptCount = this.attemptCount + 1;
                runnable.run();
            });
        }

        @Override
        public void doWait(Interval interval) {
            delegate.doWait(interval);
        }

        public int getAttemptCount() {
            return attemptCount;
        }

        @Override
        public boolean isPaused() {
            return delegate.isPaused();
        }

        @Override
        public void pause() {
            delegate.pause();
        }

        @Override
        public void resume() {
            delegate.resume();
        }
    }

}
