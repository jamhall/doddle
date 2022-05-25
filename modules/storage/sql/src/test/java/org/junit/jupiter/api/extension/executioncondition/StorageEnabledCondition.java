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
package org.junit.jupiter.api.extension.executioncondition;

import dev.doddle.storage.common.StorageProvider;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;

public class StorageEnabledCondition implements ExecutionCondition {

    private final Class<? extends StorageProvider> adapter;

    public StorageEnabledCondition(final Class<? extends StorageProvider> adapter) {
        this.adapter = adapter;
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        try {
            final InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("adapters.properties");
            final Properties properties = new Properties();
            properties.load(inputStream);
            final List<String> adapters = asList(properties.getProperty("adapters").split(","));
            if (adapters.contains(adapter.getSimpleName())) {
                return enabled(format("Test enabled for the adapter %s", adapter.getSimpleName()));
            }
            return disabled(format("Test disabled for the adapter %s", adapter.getSimpleName()));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
