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

import com.google.inject.Injector;
import dev.doddle.storage.common.StorageProvider;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;
import static java.lang.String.format;
import static org.apache.ibatis.io.Resources.getResourceAsReader;

public class InjectionExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static Injector                         injector;
    private final  Class<? extends StorageProvider> adapter;

    public InjectionExtension(Class<? extends StorageProvider> adapter) {
        this.adapter = adapter;
        injector = getOrCreateInjector();
    }

    @Override
    public void afterEach(ExtensionContext context) throws IOException {
        final StorageProvider instance = injector.getInstance(adapter);
        instance.load(getResourceAsReader(format("%s/reset.sql", instance.getName())));
    }

    @Override
    public void beforeEach(ExtensionContext context) throws IOException {
        final StorageProvider instance = injector.getInstance(adapter);
        instance.load(getResourceAsReader(format("%s/schema.sql", instance.getName())));
        instance.load(getResourceAsReader(format("%s/fixtures.sql", instance.getName())));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        return injector.getInstance(adapter);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == StorageProvider.class;
    }

    private Injector getOrCreateInjector() {
        if (injector == null) {
            injector = createInjector(new InjectionModule());
        }
        return injector;
    }
}
