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
package dev.doddle.core.engine.task;

import dev.doddle.core.engine.retry.Retryer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Method;

public class TaskDescriptor {

    private String   name;
    private String   description;
    private Method   method;
    private Class<?> enclosingClass;
    private Boolean  executionContextRequested;
    private Retryer  retryer;
    private Long     timeout;

    public TaskDescriptor() {

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (object == null || getClass() != object.getClass()) return false;

        TaskDescriptor that = (TaskDescriptor) object;

        return new EqualsBuilder()
            .append(name, that.name)
            .isEquals();
    }


    public String getDescription() {
        return description;
    }

    public TaskDescriptor setDescription(String description) {
        this.description = description;
        return this;
    }

    public Class<?> getEnclosingClass() {
        return enclosingClass;
    }

    public TaskDescriptor setEnclosingClass(Class<?> clazz) {
        this.enclosingClass = clazz;
        return this;
    }

    public Boolean getExecutionContextRequested() {
        return executionContextRequested;
    }

    public TaskDescriptor setExecutionContextRequested(Boolean executionContextRequested) {
        this.executionContextRequested = executionContextRequested;
        return this;
    }


    public Method getMethod() {
        return method;
    }

    public TaskDescriptor setMethod(Method method) {
        this.method = method;
        return this;
    }

    public String getName() {
        return name;
    }

    public TaskDescriptor setName(String name) {
        this.name = name;
        return this;
    }

    public Retryer getRetryer() {
        return retryer;
    }

    public TaskDescriptor setRetryer(Retryer retryer) {
        this.retryer = retryer;
        return this;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(name)
            .toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("name", name)
            .append("description", description)
            .append("method", method)
            .append("enclosingClass", enclosingClass)
            .append("executionContextRequested", executionContextRequested)
            .append("retryer", retryer)
            .append("timeout", timeout)
            .toString();
    }


}
