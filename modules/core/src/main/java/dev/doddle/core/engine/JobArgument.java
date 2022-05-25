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
package dev.doddle.core.engine;

import dev.doddle.core.exceptions.DoddleException;
import dev.doddle.core.support.Argument;

public class JobArgument {
    private final Argument argument;

    public JobArgument(Argument argument) {
        this.argument = argument;
    }

    public JobArgument() {
        this.argument = null;
    }

    public Integer asInt() {
        try {
            if (argument == null) {
                return null;
            }
            return Integer.parseInt(argument.getValue());
        } catch (NumberFormatException exception) {
            throw new DoddleException("argument is not an int");
        }
    }

    public Long asLong() {
        try {
            if (argument == null) {
                return null;
            }
            return Long.parseLong(argument.getValue());
        } catch (NumberFormatException exception) {
            throw new DoddleException("argument is not a long");
        }
    }


    public String asString() {
        if (argument == null) {
            return null;
        }
        return argument.getValue();
    }

    public Double asDouble() {
        try {
            if (argument == null) {
                return null;
            }
            return Double.parseDouble(argument.getValue());
        } catch (NumberFormatException exception) {
            throw new DoddleException("argument is not a double");
        }
    }

    public Boolean asBoolean() {
        if (argument == null) {
            return null;
        }
        return Boolean.parseBoolean(argument.getValue());

    }

}


