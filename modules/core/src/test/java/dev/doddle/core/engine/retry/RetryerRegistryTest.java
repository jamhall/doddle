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
package dev.doddle.core.engine.retry;

import dev.doddle.core.engine.retry.strategies.ConstantRetryStrategy;
import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import dev.doddle.core.exceptions.DoddleValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RetryerRegistryTest {

    @DisplayName("it should create a new retry registry")
    @Test
    void it_should_create_a_new_retry_registry() {
        final List<Retryer> strategies = new ArrayList<>() {{
            add(new Retryer("linear", new LinearRetryStrategy()));
            add(new Retryer("constant", new ConstantRetryStrategy()));
        }};
        final RetryerRegistry registry = new RetryerRegistry(strategies, "linear");
        assertEquals(2, registry.countAll());
        assertEquals(2, registry.getAll().size());
        assertTrue(registry.isRegistered("linear"));
        assertNotNull(registry.getForName("linear"));
        assertNull(registry.getForName("noop"));
        assertThrows(DoddleValidationException.class, () -> new RetryerRegistry(new ArrayList<>(), "linear"));
        assertThrows(NullPointerException.class, () -> new RetryerRegistry(null, "linear"));
    }

}
