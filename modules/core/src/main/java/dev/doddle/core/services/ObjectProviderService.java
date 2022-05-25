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
package dev.doddle.core.services;

import dev.doddle.common.support.NotNull;
import dev.doddle.common.support.Provides;
import dev.doddle.common.support.Singleton;
import dev.doddle.core.exceptions.DoddleValidationException;
import dev.doddle.core.modules.ObjectProviderModule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static dev.doddle.core.support.Objects.requireNonNull;

public class ObjectProviderService {

    private final Map<Class<?>, Object> singletonInstances = new HashMap<>();
    private final ObjectProviderModule  module;

    /**
     * Create a new object provider service
     *
     * @param module the module which provides the object definitions
     */
    public ObjectProviderService(final ObjectProviderModule module) {
        this.module = requireNonNull(module, "module cannot be null");
    }

    /**
     * Get an instance for a given class type
     *
     * @param clazz the class type for the instance that will be created
     * @param <T>   the generic type of the class
     * @return an instance of the given type
     * @throws DoddleValidationException if there was an error instantiating the given class
     */
    public <T> T getInstance(@NotNull final Class<T> clazz) {
        return getOrCreateInstance(clazz);
    }

    public boolean isSupported(final Class<?> clazz) {
        final Stream<Method> methods = stream(this.module.getClass().getDeclaredMethods());
        return methods
            .filter(method -> method.getReturnType() == clazz)
            .anyMatch(this::isProvider);
    }

    /**
     * Create a new instance for the given provider
     *
     * @param method the method to invoke
     * @param <T>    the generic type for the class
     * @return a new instance
     * @throws RuntimeException if the method could not be invoked
     */
    @SuppressWarnings("unchecked")
    private <T> T createInstance(@NotNull final Method method) {
        try {
            final Object[] parameters = getParametersForMethod(method);
            return (T) method.invoke(this.module, parameters);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(
                format("Could not invoke method for provider %s", method.getReturnType().getSimpleName())
            );
        }
    }

    /**
     * Get or create an instance for the given class
     *
     * @param clazz the class to create or get
     * @param <T>   the generic type for the class
     * @return an instance of the requested class
     * @throws DoddleValidationException if there was an error instantiating the class
     */
    @SuppressWarnings("unchecked")
    private <T> T getOrCreateInstance(@NotNull final Class<T> clazz) {
        final Method method = this.getProviderForClassType(clazz);
        if (isSingleton(method)) {
            if (singletonInstances.containsKey(clazz)) {
                return (T) singletonInstances.get(clazz);
            }
        }
        final T instance = createInstance(method);
        if (instance == null) {
            throw new DoddleValidationException(
                format("Could not create instance for class: %s", clazz.getSimpleName())
            );
        }
        if (this.isSingleton(method)) {
            singletonInstances.put(clazz, instance);
        }
        return instance;
    }

    /**
     * Get the parameters for a method and create an instance of a
     * parameter if necessary
     *
     * @param method the method
     * @return an array of parameter objects
     */
    private Object[] getParametersForMethod(@NotNull final Method method) {
        return stream(method.getParameters())
            .map(parameter -> getOrCreateInstance(parameter.getType()))
            .toArray();
    }

    /**
     * Get the provider for the given class type
     *
     * @param clazz the class to find
     * @param <T>   the generic type of the class
     * @return the provider method if found
     * @throws DoddleValidationException if a provider cannot be found
     */
    private <T> Method getProviderForClassType(@NotNull final Class<T> clazz) {
        final Stream<Method> methods = stream(this.module.getClass().getDeclaredMethods());
        return methods
            .filter(method -> method.getReturnType() == clazz)
            .filter(this::isProvider)
            .findFirst()
            .orElseGet(() -> {
                throw new DoddleValidationException(
                    format("Could not find provider for class: %s", clazz.getSimpleName())
                );
            });
    }

    /**
     * Is the the method a provider?
     *
     * @param method the method to check
     * @return true if a provider, otherwise false
     */
    private boolean isProvider(@NotNull Method method) {
        return method.isAnnotationPresent(Provides.class);
    }

    /**
     * Is the provider a singleton?
     *
     * @param method the method to check
     * @return true if a singleton, otherwise false
     */
    private boolean isSingleton(@NotNull Method method) {
        return method.isAnnotationPresent(Singleton.class);
    }
}
