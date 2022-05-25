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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskParameterValidatorTest {

    @Test
    @DisplayName("it should fail to validate a task method with invalid parameter")
    void it_should_fail_to_validate_a_Task_method_with_invalid_parameters() {
        final TaskMethodValidator validator = new TaskMethodValidator();
        final Class<?>[] parameters = {String.class};
        assertThrows(DoddleValidationException.class, () -> validator.isValid(parameters));
    }

    @Test
    @DisplayName("it should fail to validate a task method with more than one parameter")
    void it_should_fail_to_validate_a_Task_method_with_more_than_one_parameter() {
        final TaskMethodValidator validator = new TaskMethodValidator();
        final Class<?>[] parameters = {JobExecutionContext.class, String.class};
        assertThrows(DoddleValidationException.class, () -> validator.isValid(parameters));
    }

    @Test
    @DisplayName("it should validate a task method with no parameters")
    void it_should_validate_task_method_with_no_parameters() {
        final TaskMethodValidator validator = new TaskMethodValidator();
        final Class<?>[] parameters = {};
        assertTrue(validator.isValid(parameters));
    }

    @Test
    @DisplayName("it should validate a task method with only context parameter")
    void it_should_validate_task_method_with_only_context_parameter() {
        final TaskMethodValidator validator = new TaskMethodValidator();
        final Class<?>[] parameters = {JobExecutionContext.class};
        assertTrue(validator.isValid(parameters));
    }

}
