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

import dev.doddle.common.support.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobEnvironment implements Cloneable {

    public final Map<String, Object> environment;

    /**
     * Create a new job environment
     *
     * @param environment the environment to use
     */
    public JobEnvironment(Map<String, Object> environment) {
        this.environment = environment;
    }

    /**
     * Create a new job environment
     */
    public JobEnvironment() {
        // probably doesn't need to be thread safe as the tasks can't write to it...but just in case...
        this.environment = new ConcurrentHashMap<>();
    }

    @Override
    public JobEnvironment clone() {
        return new JobEnvironment(new HashMap<>(this.environment));
    }

    /**
     * Count all registered keys
     *
     * @return the number of keys registered
     */
    public int count() {
        return environment.size();
    }

    /**
     * Get a value by a given key
     *
     * @param key the key to search for
     * @return the value if it exists, otherwise null
     */
    public Object getByKey(String key) {
        return environment.getOrDefault(key, null);
    }

    /**
     * Add or update an existing key
     *
     * @param key   the to add or update
     * @param value the value
     * @return this
     */
    public JobEnvironment put(@NotNull final String key, @NotNull final Object value) {
        if (environment.containsKey(key)) {
            environment.replace(key, value);
        } else {
            environment.put(key, value);
        }
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append(environment)
            .toString();
    }
}
