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
package dev.doddle.storage.common.domain;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class JobFilter {

    private List<JobState> states;

    private List<String> queues;

    private List<String> tags;

    private String name;

    private String identifier;

    public JobFilter() {

    }


    public JobFilter(final List<JobState> states) {
        this.states = requireNonNull(states, "states cannot be null");
    }


    public JobFilter(final List<JobState> states, final List<String> tags) {
        this.states = requireNonNull(states, "states cannot be null");
        this.tags = requireNonNull(tags, "tags cannot be null");
    }

    public List<String> getQueues() {
        return queues;
    }

    public void setQueues(List<String> queues) {
        if (queues != null && queues.size() > 0) {
            this.queues = queues;
        }
    }

    public List<JobState> getStates() {
        return states;
    }

    public void setStates(final List<JobState> states) {
        if (states != null && states.size() > 0) {
            this.states = requireNonNull(states, "states cannot be null");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(final List<String> tags) {
        this.tags = requireNonNull(tags, "tags cannot be null");
    }


}
