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

public class Pageable {

    private long offset;
    private long limit;

    /**
     * Create a new pageable
     */
    public Pageable() {
        this.offset = 0;
        this.limit = 2147483647;
    }

    /**
     * Create a new pageable
     *
     * @param offset the offset to use
     * @param limit  the limit to use
     */
    public Pageable(long offset, long limit) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be less than zero");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit cannot be less than zero");
        }
        this.offset = offset;
        this.limit = limit;
    }

    /**
     * Get the limit
     *
     * @return the limit
     */
    public long getLimit() {
        return this.limit;
    }

    /**
     * Get the offset
     *
     * @return the offset
     */
    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }
}
