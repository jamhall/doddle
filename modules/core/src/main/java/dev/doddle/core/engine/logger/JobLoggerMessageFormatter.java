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

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static java.lang.String.valueOf;

public class JobLoggerMessageFormatter {

    private static final char   DELIM_START = '{';
    private static final String DELIM_STR   = "{}";
    private static final char   ESCAPE_CHAR = '\\';

    public JobLoggerMessageEvent format(final String level, final String pattern, final Object argument) {
        return format(level, pattern, new Object[]{argument});
    }

    public JobLoggerMessageEvent format(final String level, final String pattern, final Object argument1, final Object argument2) {
        return format(level, pattern, new Object[]{argument1, argument2});
    }

    public JobLoggerMessageEvent format(final String level, final String pattern, final Object... arguments) {
        final Throwable throwableCandidate = getThrowableCandidate(arguments);
        Object[] args = arguments;
        if (throwableCandidate != null) {
            args = trimmedCopy(arguments);
        }
        return format(level, pattern, args, throwableCandidate);
    }

    public JobLoggerMessageEvent format(final String level, final String pattern, final Object[] arguments, final Throwable throwable) {

        if (pattern == null) {
            return new JobLoggerMessageEvent(level, null, arguments, throwable);
        }

        if (arguments == null) {
            return new JobLoggerMessageEvent(level, pattern);
        }

        int i = 0;
        int j;
        StringBuilder sb = new StringBuilder(pattern.length() + 50);

        int l;
        for (l = 0; l < arguments.length; l++) {
            j = pattern.indexOf(DELIM_STR, i);
            if (j == -1) {
                // no more variables
                if (i == 0) { // this is a simple string
                    return new JobLoggerMessageEvent(level, pattern, arguments, throwable);
                } else { // add the tail string which contains no variables and return
                    // the result.
                    sb.append(pattern, i, pattern.length());
                    return new JobLoggerMessageEvent(level, sb.toString(), arguments, throwable);
                }
            } else {
                if (isEscapedDelimeter(pattern, j)) {
                    if (isDoubleEscaped(pattern, j)) {
                        // The escape character preceding the delimiter start is
                        // itself escaped: "abc x:\\{}"
                        // we have to consume one backward slash
                        sb.append(pattern, i, j - 1);
                        sb.append(appendArgument(arguments[l], new HashMap<>()));
                        i = j + 2;
                    } else {
                        l--; // DELIM_START was escaped, thus should not be incremented
                        sb.append(pattern, i, j - 1);
                        sb.append(DELIM_START);
                        i = j + 1;
                    }
                } else {
                    // normal case
                    sb.append(pattern, i, j);
                    sb.append(appendArgument(arguments[l], new HashMap<>()));
                    i = j + 2;
                }
            }
        }
        // append the characters following the last {} pair.
        sb.append(pattern, i, pattern.length());
        return new JobLoggerMessageEvent(level, sb.toString(), arguments, throwable);
    }

    private String appendArgument(final Object object, final Map<Object[], Object> seenMap) {
        if (object == null) {
            return "null";
        } else {
            if (object.getClass().isArray()) {
                if (object instanceof boolean[]) {
                    return toString((boolean[]) object);
                } else if (object instanceof byte[]) {
                    return toString((byte[]) object);
                } else if (object instanceof char[]) {
                    return toString((char[]) object);
                } else if (object instanceof short[]) {
                    return toString((short[]) object);
                } else if (object instanceof int[]) {
                    return toString((int[]) object);
                } else if (object instanceof long[]) {
                    return toString((long[]) object);
                } else if (object instanceof float[]) {
                    return toString((float[]) object);
                } else if (object instanceof double[]) {
                    return toString((double[]) object);
                } else {
                    return toString((Object[]) object, seenMap);
                }
            } else {
                return safeObjectAppend(object);
            }
        }
    }

    private StringJoiner createStringJoiner() {
        return new StringJoiner(", ", "[", "]");
    }

    private Throwable getThrowableCandidate(final Object[] arguments) {
        return JobLoggerNormalisedParameters.getThrowableCandidate(arguments);
    }

    private boolean isDoubleEscaped(final String pattern, final int delimeterStartIndex) {
        return delimeterStartIndex >= 2 && pattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR;
    }

    private boolean isEscapedDelimeter(final String pattern, final int delimeterStartIndex) {
        if (delimeterStartIndex == 0) {
            return false;
        }
        char potentialEscape = pattern.charAt(delimeterStartIndex - 1);
        return potentialEscape == ESCAPE_CHAR;
    }

    private String safeObjectAppend(final Object object) {
        try {
            return object.toString();
        } catch (Throwable throwable) {
            return "[FAILED toString()]";
        }
    }

    private String toString(final Object[] object, final Map<Object[], Object> seenMap) {
        final StringJoiner joiner = createStringJoiner();

        if (seenMap.containsKey(object)) {
            joiner.add("...");
        } else {
            seenMap.put(object, null);
            for (final Object value : object) {
                joiner.add(appendArgument(value, seenMap));
            }
        }
        // allow repeats in siblings
        seenMap.remove(object);
        return joiner.toString();
    }

    private String toString(final boolean[] values) {
        final StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (final boolean value : values) {
            joiner.add(valueOf(value));
        }
        return joiner.toString();
    }

    private String toString(final byte[] values) {
        final StringJoiner joiner = createStringJoiner();
        for (final byte value : values) {
            joiner.add(valueOf(value));
        }
        return joiner.toString();
    }

    private String toString(final char[] values) {
        final StringJoiner joiner = createStringJoiner();
        for (final char value : values) {
            joiner.add(valueOf(value));
        }
        return joiner.toString();
    }

    private String toString(final short[] values) {
        final StringJoiner joiner = createStringJoiner();
        for (final short value : values) {
            joiner.add(valueOf(value));
        }
        return joiner.toString();
    }

    private String toString(final int[] values) {
        final StringJoiner joiner = createStringJoiner();
        for (final int value : values) {
            joiner.add(valueOf(value));
        }
        return joiner.toString();
    }

    private String toString(final long[] values) {
        final StringJoiner joiner = createStringJoiner();
        for (final long value : values) {
            joiner.add(valueOf(value));
        }
        return joiner.toString();
    }

    private String toString(final float[] values) {
        final StringJoiner joiner = createStringJoiner();
        for (final float value : values) {
            joiner.add(valueOf(value));
        }
        return joiner.toString();
    }

    private String toString(final double[] values) {
        final StringJoiner joiner = createStringJoiner();
        for (final double value : values) {
            joiner.add(valueOf(value));
        }
        return joiner.toString();
    }

    private Object[] trimmedCopy(final Object[] arguments) {
        return JobLoggerNormalisedParameters.trimmedCopy(arguments);
    }

}
