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
package dev.doddle.core.engine.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.doddle.common.support.NotNull;
import dev.doddle.common.support.Nullable;
import dev.doddle.core.engine.JobData;
import dev.doddle.core.engine.crypto.EncryptionService;
import dev.doddle.core.exceptions.MapperException;

import static dev.doddle.core.support.Objects.isNull;
import static dev.doddle.core.support.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class JacksonMapperAdapter implements MapperAdapter {

    private final ObjectMapper mapper;


    public JacksonMapperAdapter(@NotNull final ObjectMapper mapper,
                                @NotNull final EncryptionService encryptionService) {
        this.mapper = requireNonNull(mapper, "mapper cannot be null");
        final SimpleModule module = new SimpleModule();
        module.addSerializer(JobData.class, new JobArgumentSerializer(encryptionService));
        module.addDeserializer(JobData.class, new JobArgumentDeserializer(encryptionService));
        this.mapper.registerModule(module);
    }

    public JacksonMapperAdapter(@NotNull final EncryptionService encryptionService) {
        this(new ObjectMapper(), encryptionService);
    }

    @Override
    public String convertToJson(@Nullable final JobData data) throws MapperException {
        try {
            if (isNull(data)) {
                return null;
            }
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException exception) {
            throw new MapperException("Unable to convert data into a JSON string", exception);
        }
    }

    @Override
    public JobData convertToObject(@Nullable final String data) throws MapperException {
        try {
            if (isEmpty(data)) {
                return null;
            }
            return mapper.readValue(data, JobData.class);
        } catch (JsonProcessingException exception) {
            throw new MapperException("Could not deserialise job data");
        }
    }

}
