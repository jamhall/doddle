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

import dev.doddle.core.engine.JobExecutionContext;
import dev.doddle.core.exceptions.DoddleValidationException;

import java.lang.reflect.Parameter;

import static java.util.Arrays.stream;

public class TaskMethodValidator {

    private static final int MAX_NUMBER_PARAMETERS = 1;

    /**
     * Validate the task parameters
     *
     * @param parameters the parameters to validate
     * @return whether the task parameters are valid or not
     */
    public Boolean isValid(final Class<?>[] parameters) {
        if (parameters.length > MAX_NUMBER_PARAMETERS) {
            throw new DoddleValidationException("A task method cannot have more than one parameter");
        }
        if (parameters.length == 1) {
            if (!parameters[0].equals(JobExecutionContext.class)) {
                throw new DoddleValidationException(
                    "The only parameter accepted is the JobExecutionContext"
                );
            }
        }
        return true;
    }

    /**
     * Validate the task parameters
     *
     * @param parameters the parameters to validate
     * @return whether the task parameters are valid or not
     */
    public Boolean isValid(final Parameter[] parameters) {
        return isValid(
            stream(parameters)
                .map(Parameter::getType)
                .toArray(Class<?>[]::new)
        );
    }
}
