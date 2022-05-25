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
package dev.doddle.core.engine.time;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.concurrent.TimeUnit;

public class IntervalTimeUnit {

    private final String   label;
    private final TimeUnit unit;
    private final int      level;

    public IntervalTimeUnit(String label, TimeUnit unit, int level) {
        this.label = label;
        this.unit = unit;
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        IntervalTimeUnit that = (IntervalTimeUnit) o;

        return new EqualsBuilder().append(label, that.label).isEquals();
    }

    public String getLabel() {
        return label;
    }

    public int getLevel() {
        return level;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(label).toHashCode();
    }

    public boolean isLabel(final String label) {
        return this.label.equals(label);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("label", label)
            .append("unit", unit)
            .append("level", level)
            .toString();
    }
}
