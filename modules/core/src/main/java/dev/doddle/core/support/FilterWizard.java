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
package dev.doddle.core.support;

import dev.doddle.common.support.NotNull;
import dev.doddle.storage.common.domain.JobFilter;
import dev.doddle.storage.common.domain.JobState;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static dev.doddle.core.support.Objects.requireNonNullElse;

public class FilterWizard {


    private List<JobState> states;

    private List<String> queues;

    private List<String> tags;

    private String name;

    private String identifier;

    public FilterWizard() {
        this.states = new ArrayList<>();
        this.queues = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public FilterWizard states(@NotNull final List<JobState> states) {
        this.states.addAll(requireNonNullElse(states, new ArrayList<>()));
        return this;
    }

    public FilterWizard states(final JobState... states) {
        this.states.addAll(asList(requireNonNullElse(states, new JobState[]{})));
        return this;
    }

    public FilterWizard queues(@NotNull final List<String> queues) {
        this.queues.addAll(requireNonNullElse(queues, new ArrayList<>()));
        return this;
    }

    public FilterWizard queues(final String... queues) {
        this.queues.addAll(asList(requireNonNullElse(queues, new String[]{})));
        return this;
    }

    public FilterWizard name(final String name) {
        this.name = name;
        return this;
    }

    public FilterWizard identifier(final String identifier) {
        this.identifier = identifier;
        return this;
    }

    public JobFilter build() {
        final JobFilter filter = new JobFilter();
        filter.setQueues(this.queues);
        filter.setStates(this.states);
        filter.setTags(this.tags);
        filter.setName(this.name);
        filter.setIdentifier(this.identifier);
        return filter;
    }


}
