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
package dev.doddle.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.doddle.core.DoddleConfiguration;
import dev.doddle.core.engine.mapper.JacksonMapperAdapter;
import dev.doddle.core.engine.polling.PollingConfiguration;
import dev.doddle.core.engine.retry.Retryer;
import dev.doddle.core.engine.retry.strategies.LinearRetryStrategy;
import dev.doddle.core.engine.task.TaskDependencyResolver;
import dev.doddle.core.engine.task.TaskOptions;
import dev.doddle.core.engine.task.TaskParser;
import dev.doddle.core.engine.time.ticker.strategies.FakeTickerStrategy;
import dev.doddle.core.modules.ObjectProviderModule;
import dev.doddle.storage.common.NoopStorageProvider;
import dev.doddle.storage.common.Storage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;

class ObjectProviderServiceTest {

    // @FIXME
    @DisplayName("it should get a new instance")
    @Test
    void it_should_get_a_new_instance() {
//        final DoddleConfiguration configuration = new DoddleConfiguration();
//        configuration.setTickerStrategy(new FakeTickerStrategy());
//        configuration.setPollingConfiguration(new PollingConfiguration());
//        configuration.setTaskDependencyResolver(new TaskDependencyResolver() {
//            @Override
//            public <T> T resolve(Class<T> type) {
//                return null;
//            }
//        });
//        configuration.setTaskDataMapperAdapter(new JacksonMapperAdapter(new ObjectMapper()));
//        configuration.setBasePackages("dev.doddle.common.support.tasks.valid");
//        configuration.setDefaultTaskOptions(TaskOptions.createDefaultTaskOptions());
//        configuration.setRetryers(
//            new ArrayList<>() {{
//                add(new Retryer("linear", new LinearRetryStrategy()));
//            }}
//        );
//        configuration.setStorage(new Storage(new NoopStorageProvider()));
//        final ObjectProviderService provider = new ObjectProviderService(new ObjectProviderModule(configuration));
//
//        TaskParser executor1 = provider.getInstance(TaskParser.class);
//        TaskParser executor2 = provider.getInstance(TaskParser.class);
//        assertSame(executor1, executor2);
    }


}
