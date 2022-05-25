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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.doddle.core.engine.JobData;
import dev.doddle.core.engine.JobEncryption;
import dev.doddle.core.engine.crypto.EncryptionService;
import dev.doddle.core.support.Argument;

import java.io.IOException;

import static dev.doddle.core.support.Objects.requireNonNull;

public class JobArgumentSerializer extends StdSerializer<JobData> {

    private final EncryptionService encryptionService;

    public JobArgumentSerializer(final EncryptionService encryptionService) {
        super(JobData.class);
        this.encryptionService = requireNonNull(encryptionService, "encryptionService cannot be null");
    }


    @Override
    public void serialize(final JobData data,
                          final JsonGenerator generator,
                          final SerializerProvider serializer) throws IOException {
        final JobEncryption encryption = data.getEncryption();
        generator.writeStartObject();
        generator.writeObjectField("encryption", encryption);

        generator.writeArrayFieldStart("arguments");

        for (final Argument argument : data.getArguments()) {

            final String name = argument.getName();
            final String value = argument.getValue();

            generator.writeStartObject();
            generator.writeStringField("name", name);
            generator.writeBooleanField("encrypted", argument.isEncrypted());

            if (argument.isEncrypted()) {
                final byte[] encrypted = encryptionService.encrypt(value, encryption);
                generator.writeBinaryField("value", encrypted);
            } else {
                generator.writeStringField("value", value);
            }

            generator.writeEndObject();

        }

        generator.writeEndArray();
        generator.writeEndObject();
    }
}
