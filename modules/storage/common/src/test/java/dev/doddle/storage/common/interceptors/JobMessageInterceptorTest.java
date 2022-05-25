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
package dev.doddle.storage.common.interceptors;

import dev.doddle.storage.common.domain.JobMessage;
import dev.doddle.storage.common.support.FakeKeyGenerator;
import dev.doddle.storage.common.support.KeyGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobMessageInterceptorTest {

    @DisplayName("it should fail to intercept a job message because the job message is null")
    @Test
    void it_should_fail_to_intercept_a_job_message_because_the_job_message_is_null() {
        final KeyGenerator keyGenerator = new FakeKeyGenerator();
        final JobMessageInterceptor interceptor = new JobMessageInterceptor(keyGenerator);
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> interceptor.apply(null));
        assertTrue(exception1.getMessage().contains("message cannot be null"));
    }

    @DisplayName("it should intercept a job message")
    @Test
    void it_should_intercept_a_job_message() {
        final KeyGenerator keyGenerator = new FakeKeyGenerator();
        final JobMessageInterceptor interceptor = new JobMessageInterceptor(keyGenerator);
        final JobMessage message = new JobMessage();
        interceptor.apply(message);
        assertEquals("1", message.getId());
        assertNotNull(message.getCreatedAt());
    }
}
