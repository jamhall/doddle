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
package dev.doddle.core;

import dev.doddle.core.modules.ObjectProviderModule;
import dev.doddle.core.services.ObjectProviderService;

import static dev.doddle.core.support.Objects.isNull;

public class DoddleClientFactory {

    /**
     * Create a new client
     * Delegates the creation to the client provider class
     * for managing the instantiation of all of the concrete implementations
     *
     * @return a new client
     */
    public DoddleClient createClient(final DoddleConfiguration configuration) {
        if (isNull(configuration.getStorage())) {
            throw new RuntimeException("Storage must be set");
        }

        if (isNull(configuration.getTaskDependencyResolver())) {
            throw new RuntimeException("Task dependency resolver must be set");
        }

        if (isNull(configuration.getBasePackages())) {
            throw new RuntimeException("Base packages must be set");
        }
        final ObjectProviderModule module = new ObjectProviderModule(configuration);
        final ObjectProviderService provider = new ObjectProviderService(module);
        return provider.getInstance(DoddleClient.class);
    }

}
