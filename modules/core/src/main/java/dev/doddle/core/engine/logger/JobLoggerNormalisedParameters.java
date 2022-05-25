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
package dev.doddle.core.engine.logger;

import static java.lang.System.arraycopy;

public class JobLoggerNormalisedParameters {

    final String    message;
    final Object[]  arguments;
    final Throwable throwable;

    public JobLoggerNormalisedParameters(final String message, final Object[] arguments, final Throwable throwable) {
        this.message = message;
        this.arguments = arguments;
        this.throwable = throwable;
    }

    public JobLoggerNormalisedParameters(String message, Object[] arguments) {
        this(message, arguments, null);
    }

    /**
     * Helper method to determine if an {@link Object} array contains a
     * {@link Throwable} as last element
     *
     * @param arguments The arguments off which we want to know if it contains a
     *                  {@link Throwable} as last element
     * @return if the last {@link Object} in argArray is a {@link Throwable} this
     * method will return it, otherwise it returns null
     */
    public static Throwable getThrowableCandidate(final Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return null;
        }

        final Object lastEntry = arguments[arguments.length - 1];
        if (lastEntry instanceof Throwable) {
            return (Throwable) lastEntry;
        }

        return null;
    }

    /**
     * Helper method to get all but the last element of an array
     *
     * @param arguments The arguments from which we want to remove the last element
     * @return a copy of the array without the last element
     */
    public static Object[] trimmedCopy(final Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }

        final int trimmedLen = arguments.length - 1;

        final Object[] trimmed = new Object[trimmedLen];

        if (trimmedLen > 0) {
            arraycopy(arguments, 0, trimmed, 0, trimmedLen);
        }

        return trimmed;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}
