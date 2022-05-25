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
package dev.doddle.core.engine;

import dev.doddle.core.engine.scheduling.SchedulingManager;
import dev.doddle.core.engine.time.Interval;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

class SchedulingManagerTest {

    @Test
    void it_should_test_the_scheduler() {
        try {
            SchedulingManager manager = new SchedulingManager(
                new Interval(0, SECONDS),
                new Interval(500, MILLISECONDS)
            );
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    manager.stop();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                System.out.println("Thread stopping");
            });
            thread.start();
            manager.start();
            manager.join();
        } catch (InterruptedException exception) {

        }

//        int iterations = 0;
//        while (true) {
//            try {
//                Thread.sleep(1000);
//
//                iterations = iterations + 1;
//                if (iterations == 10) {
//                    break;
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

}