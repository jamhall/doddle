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

import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;

public class ScheduleBuilder {

    private final static int SUNDAY    = 0;
    private final static int MONDAY    = 1;
    private final static int TUESDAY   = 2;
    private final static int WEDNESDAY = 3;
    private final static int THURSDAY  = 4;
    private final static int FRIDAY    = 5;
    private final static int SATURDAY  = 6;

    private final String[] expression;

    public ScheduleBuilder() {
        this.expression = new String[]{"*", "*", "*", "*", "*"};
    }

    public String build() {
        return join(" ", this.expression);
    }

    public ScheduleBuilder daily() {
        return this.spliceIntoPosition(1, 0)
            .spliceIntoPosition(2, 0);
    }


    public ScheduleBuilder days(final String days) {
        return this.spliceIntoPosition(5, days);
    }

    public ScheduleBuilder everyFifteenMinutes() {
        return this.spliceIntoPosition(1, "*/15");
    }

    public ScheduleBuilder everyFiveMinutes() {
        return this.spliceIntoPosition(1, "*/5");
    }

    public ScheduleBuilder everyFourHours() {
        return this.spliceIntoPosition(1, 0)
            .spliceIntoPosition(2, "*/4");
    }

    public ScheduleBuilder everyFourMinutes() {
        return this.spliceIntoPosition(1, "*/4");
    }

    public ScheduleBuilder everyMinute() {
        return this.spliceIntoPosition(1, "*");
    }

    public ScheduleBuilder everySixHours() {
        return this.spliceIntoPosition(1, 0)
            .spliceIntoPosition(2, "*/6");
    }

    public ScheduleBuilder everyTenMinutes() {
        return this.spliceIntoPosition(1, "*/10");
    }

    public ScheduleBuilder everyThirtyMinutes() {
        return this.spliceIntoPosition(1, "0,30");
    }

    public ScheduleBuilder everyThreeHours() {
        return this.spliceIntoPosition(1, 0)
            .spliceIntoPosition(2, "*/3");
    }

    public ScheduleBuilder everyThreeMinutes() {
        return this.spliceIntoPosition(1, "*/3");
    }

    public ScheduleBuilder everyTwoHours() {
        return this.spliceIntoPosition(1, 0)
            .spliceIntoPosition(2, "*/2");
    }

    public ScheduleBuilder everyTwoMinutes() {
        return this.spliceIntoPosition(1, "*/2");
    }

    public ScheduleBuilder fridays() {
        return this.days(FRIDAY);
    }

    public ScheduleBuilder hourly() {
        return this.spliceIntoPosition(1, 0);
    }

    public ScheduleBuilder hourlyAt(final int offset) {
        return this.spliceIntoPosition(1, offset);
    }

    public ScheduleBuilder hourlyAt(final int... offset) {
        final String result = IntStream.of(offset)
            .mapToObj(Integer::toString)
            .collect(joining(","));
        return this.spliceIntoPosition(2, result);
    }

    public ScheduleBuilder days(final int... days) {

        final String result = IntStream.of(days)
            .mapToObj(Integer::toString)
            .collect(joining(","));

        return this.spliceIntoPosition(5, result);
    }

    public ScheduleBuilder dailyAt(final String time) {
        final String[] segments = time.split(":");
        return this.spliceIntoPosition(2, segments[0])
            .spliceIntoPosition(1, segments.length == 2 ? segments[1] : "0");
    }

    public ScheduleBuilder at(final String time) {
        return this.dailyAt(time);
    }

    public ScheduleBuilder mondays() {
        return this.days(MONDAY);
    }

    /**
     * Schedule the event to run monthly.
     *
     * @return this
     */
    public ScheduleBuilder monthly() {
        return this.spliceIntoPosition(1, 0)
            .spliceIntoPosition(2, 0)
            .spliceIntoPosition(3, 1);
    }

    /**
     * Schedule the event to run quarterly.
     *
     * @return this
     */
    public ScheduleBuilder quarterly() {
        return this.spliceIntoPosition(1, 0)
            .spliceIntoPosition(2, 0)
            .spliceIntoPosition(3, 1)
            .spliceIntoPosition(4, "1-12/3");
    }

    public ScheduleBuilder reset() {
        Arrays.fill(this.expression, "*");
        return this;
    }

    public ScheduleBuilder saturdays() {
        return this.days(SATURDAY);
    }

    public ScheduleBuilder sundays() {
        return this.days(SUNDAY);
    }

    public ScheduleBuilder thursdays() {
        return this.days(THURSDAY);
    }

    public ScheduleBuilder tuesdays() {
        return this.days(TUESDAY);
    }

    public ScheduleBuilder wednesdays() {
        return this.days(WEDNESDAY);
    }

    public ScheduleBuilder weekdays() {
        return this.days(format("%d-%d", MONDAY, FRIDAY));
    }

    public ScheduleBuilder weekends() {
        return this.days(format("%d,%d", SATURDAY, SUNDAY));
    }

    public ScheduleBuilder weekly() {
        return this.spliceIntoPosition(1, 0)
            .spliceIntoPosition(2, 0)
            .spliceIntoPosition(5, 0);
    }

    public ScheduleBuilder weeklyOn(final String dayOfWeek, final String time) {
        this.dailyAt(time);
        return this.days(dayOfWeek);
    }

    public ScheduleBuilder weeklyOn(final String dayOfWeek) {
        return this.weeklyOn(dayOfWeek, "0:0");
    }


    public ScheduleBuilder monthlyOn(final int dayOfMonth, final String time) {
        this.dailyAt(time);
        return this.spliceIntoPosition(3, dayOfMonth);
    }

    public ScheduleBuilder monthlyOn(final int dayOfMonth) {
        return this.monthlyOn(dayOfMonth, "0:0");
    }

    public ScheduleBuilder yearly() {
        return this.spliceIntoPosition(1, 0)
            .spliceIntoPosition(2, 0)
            .spliceIntoPosition(3, 1)
            .spliceIntoPosition(4, 1);
    }

    private ScheduleBuilder spliceIntoPosition(final int position, final String value) {
        this.expression[position - 1] = value;
        return this;
    }

    private ScheduleBuilder spliceIntoPosition(final int position, final int... value) {
        final String result = IntStream.of(value)
            .mapToObj(Integer::toString)
            .collect(joining(","));
        return spliceIntoPosition(position, result);
    }


}
