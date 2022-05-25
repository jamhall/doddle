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
package dev.doddle.core.engine.task;

import dev.doddle.common.support.NotNull;
import dev.doddle.common.support.Nullable;
import dev.doddle.core.exceptions.TaskParserException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isPublic;
import static dev.doddle.core.support.Objects.requireNonNull;
import static dev.doddle.core.support.Objects.requireNonNullElse;
import static org.reflections.scanners.Scanners.MethodsAnnotated;
import static org.reflections.util.ClasspathHelper.forPackage;

public class TaskParser {

    private final TaskMethodValidator   taskParameterValidator;
    private final TaskDescriptorFactory taskDescriptorFactory;

    /**
     * Create a new parser
     *
     * @param taskMethodValidator   the task method validator
     * @param taskDescriptorFactory the task descriptor builder
     */
    public TaskParser(@NotNull final TaskDescriptorFactory taskDescriptorFactory,
                      @Nullable final TaskMethodValidator taskMethodValidator) {

        this.taskParameterValidator = requireNonNullElse(taskMethodValidator, new TaskMethodValidator());
        this.taskDescriptorFactory = taskDescriptorFactory;
    }

    /**
     * Create a new parser
     *
     * @param taskDescriptorFactory the task descriptor factory
     */
    public TaskParser(@NotNull final TaskDescriptorFactory taskDescriptorFactory) {
        this(taskDescriptorFactory, null);
    }

    /**
     * Find and parse tasks
     *
     * @param basePackages the base packages to scan for tasks
     * @return a list of parsed tasks
     */
    public List<TaskDescriptor> parse(@NotNull final String basePackages) {
        final Collection<URL> paths = forPackage(requireNonNull(basePackages, "basePackages cannot be null"));

        if (paths.size() == 0) {
            throw new TaskParserException(format("Invalid base packages path supplied: %s", basePackages));
        }

        final List<TaskDescriptor> tasks = new ArrayList<>();
        final Stream<Method> methods = getMethods(createReflections(paths, basePackages));
        methods.map(method -> {
            final Task annotation = getAnnotationForMethod(method);
            return taskDescriptorFactory.createTaskDescriptor(method, annotation);
        }).forEach(task -> {
            if (tasks.contains(task)) {
                throw new TaskParserException(
                    format("A duplicate task has been found for: %s. Task names must be unique", task.getName())
                );
            }
            tasks.add(task);
        });
        return tasks;
    }

    /**
     * Set up the reflections
     *
     * @param paths        the paths
     * @param basePackages the base packages to scan
     * @return the reflections
     */
    private Reflections createReflections(final Collection<URL> paths, final String basePackages) {
        return new Reflections(new ConfigurationBuilder()
            .setUrls(paths)
            .filterInputsBy(new FilterBuilder().includePackage(basePackages))
            .setScanners(MethodsAnnotated)
        );
    }

    /**
     * Get the {@link Task} annotation for a given method
     *
     * @param method the class method
     * @return the task details
     */
    private Task getAnnotationForMethod(@NotNull final Method method) {
        return method.getAnnotation(Task.class);
    }

    /**
     * Get all valid task methods
     *
     * @param reflections the found reflections
     * @return a stream of found methods
     */
    private Stream<Method> getMethods(final Reflections reflections) {
        return reflections.getMethodsAnnotatedWith(Task.class)
            .stream()
            .filter(method -> method.getReturnType().equals(void.class))
            .filter(method -> taskParameterValidator.isValid(method.getParameters()))
            .filter(method -> isPublic(method.getModifiers()));
    }
}
