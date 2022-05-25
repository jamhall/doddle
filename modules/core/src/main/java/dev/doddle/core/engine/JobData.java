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

import dev.doddle.core.support.Argument;

import java.util.ArrayList;
import java.util.List;

import static dev.doddle.core.support.Objects.requireNonNullElse;

public class JobData {

    private List<Argument> arguments;
    private JobEncryption  encryption;

    public JobData(
        final JobEncryption encryption,
        final List<Argument> arguments

    ) {
        this.encryption = encryption;
        this.arguments = requireNonNullElse(arguments, new ArrayList<>());
    }

    public JobData(final JobEncryption encryption) {
        this(encryption, new ArrayList<>());
    }

    public List<Argument> getArguments() {
        return this.arguments;
    }

    public JobEncryption getEncryption() {
        return encryption;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public void setEncryption(JobEncryption encryption) {
        this.encryption = encryption;
    }

    public void addArgument(Argument argument) {
        this.arguments.add(argument);
    }
}
