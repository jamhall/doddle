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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.doddle.core.engine.JobData;
import dev.doddle.core.engine.JobEncryption;
import dev.doddle.core.engine.crypto.EncryptionService;
import dev.doddle.core.exceptions.DoddleException;
import dev.doddle.core.support.Argument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static dev.doddle.core.support.Argument.argument;
import static dev.doddle.core.support.Objects.requireNonNull;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JobArgumentDeserializer extends StdDeserializer<JobData> {

    private final EncryptionService encryptionService;

    public JobArgumentDeserializer(final EncryptionService encryptionService) {
        super(JobData.class);
        this.encryptionService = requireNonNull(encryptionService, "encryptionService cannot be null");
    }

    @Override
    public JobData deserialize(final JsonParser parser,
                               final DeserializationContext context) throws IOException {
        final JsonNode root = parser.getCodec().readTree(parser);

        if (root.isEmpty()) {
            return null;
        }

        final JobEncryption encryption = readEncryptionNode(context, root);
        final List<Argument> arguments = readArguments(context, root, encryption);

        return new JobData(encryption, arguments);
    }

    private List<Argument> readArguments(final DeserializationContext context,
                                         final JsonNode root,
                                         final JobEncryption encryption) {
        try {
            if (root.has("arguments")) {
                final List<Argument> args = new ArrayList<>();
                final JsonNode arguments = root.get("arguments");
                for (final JsonNode node : arguments) {
                    final Argument argument = context.readTreeAsValue(node, Argument.class);
                    if (argument.isEncrypted()) {
                        if (encryptionService.isEnabled()) {
                            final byte[] bytes = Base64.getDecoder().decode(argument.getValue());
                            final byte[] decrypted = encryptionService.decrypt(bytes, encryption);
                            args.add(argument(argument.getName(), new String(decrypted, UTF_8), true));
                        } else {
                            throw new DoddleException("Cannot decrypt an encrypted argument because encryption is disabled");
                        }
                    } else {
                        args.add(argument(argument.getName(), argument.getValue(), false));
                    }
                }
                return args;
            }

            return new ArrayList<>();
        } catch (IOException exception) {
            throw new DoddleException("Could not deserialise job arguments", exception);
        }
    }

    private JobEncryption readEncryptionNode(final DeserializationContext context, final JsonNode root) throws IOException {
        final JsonNode encryption = root.get("encryption");
        if (!encryption.isNull()) {
            return context.readTreeAsValue(
                root.get("encryption"),
                JobEncryption.class
            );
        }
        return null;
    }
}
