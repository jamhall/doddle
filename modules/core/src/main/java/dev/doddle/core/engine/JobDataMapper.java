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

import dev.doddle.common.support.NotNull;
import dev.doddle.common.support.Nullable;
import dev.doddle.core.engine.mapper.MapperAdapter;
import dev.doddle.core.exceptions.MapperException;

import static dev.doddle.core.support.Objects.requireNonNull;

public class JobDataMapper {

    private final MapperAdapter adapter;

    /**
     * Create a new task mapper
     *
     * @param adapter the serialization adapter to use
     */
    public JobDataMapper(@NotNull final MapperAdapter adapter) {
        this.adapter = requireNonNull(adapter, "adapter cannot be null");
    }

    /**
     * Converts the payload object into a json string
     *
     * @param data the payload object to convert
     * @return the converted json
     * @throws MapperException thrown if there was an error during conversion
     */
    public String convertToJson(@Nullable final JobData data) throws MapperException {
        if (data == null) {
            return null;
        }
        return adapter.convertToJson(data);
    }


    public JobData convertToObject(@Nullable final String data) throws MapperException {
        if (data == null) {
            return null;
        }
        return adapter.convertToObject(data);
    }

}
