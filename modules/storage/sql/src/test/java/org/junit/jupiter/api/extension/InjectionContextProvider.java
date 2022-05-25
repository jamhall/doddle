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
package org.junit.jupiter.api.extension;

import dev.doddle.storage.common.StorageProvider;
import dev.doddle.storage.sql.MySQLStorageProvider;
import dev.doddle.storage.sql.PostgresStorageProvider;
import org.junit.jupiter.api.extension.executioncondition.StorageEnabledCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class InjectionContextProvider implements TestTemplateInvocationContextProvider {

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
        final Map<Class<? extends StorageProvider>, String> adapters = new HashMap<>() {{
            put(PostgresStorageProvider.class, "postgres");
            put(MySQLStorageProvider.class, "mysql");
        }};
        return adapters.entrySet().stream().map(adapter -> createContext(adapter.getKey(), adapter.getValue()));
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext extensionContext) {
        return true;
    }

    private TestTemplateInvocationContext createContext(Class<? extends StorageProvider> key, final String name) {
        return new TestTemplateInvocationContext() {

            @Override
            public List<Extension> getAdditionalExtensions() {
                return asList(
                    new StorageEnabledCondition(key),
                    new InjectionExtension(key)
                );
            }

            @Override
            public String getDisplayName(int invocationIndex) {
                return name;
            }
        };
    }
}
