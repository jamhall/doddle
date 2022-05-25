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

import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.support.FakeKeyGenerator;
import dev.doddle.storage.common.support.KeyGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobInterceptorTest {

    @DisplayName("it should fail to intercept a job because the job is null")
    @Test
    void it_should_fail_to_intercept_a_job_because_the_job_is_null() {
        final KeyGenerator keyGenerator = new FakeKeyGenerator();
        final JobInterceptor interceptor = new JobInterceptor(keyGenerator);
        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> interceptor.apply(null));
        assertTrue(exception1.getMessage().contains("job cannot be null"));
    }

    @DisplayName("it should intercept a job")
    @Test
    void it_should_intercept_a_job() {
        final KeyGenerator keyGenerator = new FakeKeyGenerator();
        final JobInterceptor interceptor = new JobInterceptor(keyGenerator);
        final Job job = new Job();
        interceptor.apply(job);
        assertEquals("1", job.getId());
        assertNotNull(job.getCreatedAt());
    }
}
