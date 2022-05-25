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

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JobStatistic {

    private Long total;
    private Long available;
    private Long scheduled;
    private Long executing;
    private Long retryable;
    private Long completed;
    private Long discarded;
    private Long failed;

    public JobStatistic() {

    }

    public Long getAvailable() {
        return available;
    }

    public void setAvailable(Long available) {
        this.available = available;
    }

    public Long getCompleted() {
        return completed;
    }

    public void setCompleted(Long completed) {
        this.completed = completed;
    }

    public Long getDiscarded() {
        return discarded;
    }

    public void setDiscarded(Long discarded) {
        this.discarded = discarded;
    }

    public Long getExecuting() {
        return executing;
    }

    public void setExecuting(Long executing) {
        this.executing = executing;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    public Long getRetryable() {
        return retryable;
    }

    public void setRetryable(Long retryable) {
        this.retryable = retryable;
    }

    public Long getScheduled() {
        return scheduled;
    }

    public void setScheduled(Long scheduled) {
        this.scheduled = scheduled;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("total", total)
            .append("available", available)
            .append("scheduled", scheduled)
            .append("executing", executing)
            .append("retryable", retryable)
            .append("completed", completed)
            .append("discarded", discarded)
            .append("failed", failed)
            .toString();
    }

}

