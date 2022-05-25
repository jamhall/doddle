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
package dev.doddle.core.engine.task.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.doddle.core.engine.mapper.JacksonMapperAdapter;
import dev.doddle.core.exceptions.MapperException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonJobDataMapperAdapterTest {

    @DisplayName("it should fail to construct the converter because the mapper is null")
    @Test
    void it_should_fail_to_construct_the_converter_because_the_mapper_is_null() {
        final NullPointerException exception = assertThrows(NullPointerException.class, () -> new JacksonMapperAdapter(null));
        assertEquals("mapper cannot be null", exception.getMessage());
    }

    // @FIXME

    @DisplayName("it should fail to convert the data into an object")
    @Test
    void it_should_fail_to_convert_data_into_an_object() {
//        final ObjectMapper mapper = new ObjectMapper();
//
//        final JacksonMapperAdapter converter = new JacksonMapperAdapter(mapper);
//
//        final NullPointerException exception1 = assertThrows(NullPointerException.class, () -> {
//            converter.convertToObject("{\"name\":\"Hello world!\"}", null);
//        });
//
//        assertEquals("object cannot be null", exception1.getMessage());
//
//        final MapperException exception2 = assertThrows(MapperException.class, () -> {
//            converter.convertToObject("{\"name\":\"Hello world!\"}", IncoherentMessage.class);
//        });
//
//        assertEquals("Could not deserialise data into the given object: IncoherentMessage", exception2.getMessage());

    }

    // FIXME
    @DisplayName("it should fail to convert the data into json")
    @Test
    void it_should_fail_to_convert_data_into_json() {
//        final ObjectMapper mapper = new ObjectMapper();
//        final JacksonMapperAdapter converter = new JacksonMapperAdapter(mapper);
//        assertDoesNotThrow(() -> {
//            final String json = converter.convertToJson(null);
//            assertNull(json);
//        });
//        assertThrows(MapperException.class, () -> converter.convertToJson(new InvalidMessage()));
    }

    // FIXME
    @DisplayName("it should successfully convert the data into an object")
    @Test
    void it_should_successfully_convert_data_into_an_object() {
//        final ObjectMapper mapper = new ObjectMapper();
//        final JacksonMapperAdapter converter = new JacksonMapperAdapter(mapper);
//        assertDoesNotThrow(() -> {
//            final Message message = converter.convertToObject("{\"name\":\"Hello world!\"}", Message.class);
//            assertEquals("Hello world!", message.getName());
//        });
    }

    // FIXME
    @DisplayName("it should successfully convert the data into json")
    @Test
    void it_should_successfully_convert_data_into_json() {
//        final ObjectMapper mapper = new ObjectMapper();
//        final JacksonMapperAdapter converter = new JacksonMapperAdapter(mapper);
//
//        assertDoesNotThrow(() -> {
//            final String json = converter.convertToJson(new Message("Hello world!"));
//            assertEquals("{\"name\":\"Hello world!\"}", json);
//        });
    }

    // this class is invalid because there is no constructor and therefore jackson will fail to convert it
    public static class InvalidMessage {

    }

    // stub class for testing
    public static class Message {

        private String name;

        public Message() {

        }

        Message(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // stub class for testing
    public static class IncoherentMessage {
        public IncoherentMessage() {

        }
    }

}
