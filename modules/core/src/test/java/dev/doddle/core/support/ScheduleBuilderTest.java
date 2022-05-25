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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScheduleBuilderTest {

    @DisplayName("it should test basic frequencies")
    @Test
    void it_should_test_basic_frequencies() {
        final ScheduleBuilder builder = new ScheduleBuilder();

        final Map<String, Supplier<ScheduleBuilder>> frequencies = new HashMap<>() {{
            put("* * * * *", builder::everyMinute);
            put("*/2 * * * *", builder::everyTwoMinutes);
            put("*/3 * * * *", builder::everyThreeMinutes);
            put("*/4 * * * *", builder::everyFourMinutes);
            put("*/5 * * * *", builder::everyFiveMinutes);
            put("*/10 * * * *", builder::everyTenMinutes);
            put("*/15 * * * *", builder::everyFifteenMinutes);
            put("0,30 * * * *", builder::everyThirtyMinutes);
            put("0 * * * *", builder::hourly);
            put("0 */2 * * *", builder::everyTwoHours);
            put("0 */3 * * *", builder::everyThreeHours);
            put("* * * * 1-5", builder::weekdays);
            put("* * * * 6,0", builder::weekends);
            put("* * * * 1", builder::mondays);
            put("* * * * 2", builder::tuesdays);
            put("* * * * 3", builder::wednesdays);
            put("* * * * 4", builder::thursdays);
            put("* * * * 5", builder::fridays);
            put("* * * * 6", builder::saturdays);
            put("* * * * 0", builder::sundays);
            put("0 0 * * 0", builder::weekly);
            put("0 0 1 * *", builder::monthly);
            put("0 0 1 1-12/3 *", builder::quarterly);
            put("0 0 1 1 *", builder::yearly);
            put("0 0 * * *", builder::daily);
        }};

        frequencies.forEach((key, value) -> {
            assertEquals(key, value.get().build());
            builder.reset();
        });


    }

    @DisplayName("it should test every five minutes")
    @Test
    void it_should_test_every_five_minutes() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.everyFiveMinutes().build();
        assertEquals("*/5 * * * *", expression);
    }

    @DisplayName("it should test every four minutes")
    @Test
    void it_should_test_every_four_minutes() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.everyFourMinutes().build();
        assertEquals("*/4 * * * *", expression);
    }

    @DisplayName("it should test every minute")
    @Test
    void it_should_test_every_minute() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.everyMinute().build();
        assertEquals("* * * * *", expression);
    }

    @DisplayName("it should test every three minutes")
    @Test
    void it_should_test_every_three_minutes() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.everyThreeMinutes().build();
        assertEquals("*/3 * * * *", expression);
    }

    @DisplayName("it should test every two hours")
    @Test
    void it_should_test_every_two_hours() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.everyTwoHours().build();
        assertEquals("0 */2 * * *", expression);
    }

    @DisplayName("it should test every two minutes")
    @Test
    void it_should_test_every_two_minutes() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.everyTwoMinutes().build();
        assertEquals("*/2 * * * *", expression);
    }

    @DisplayName("it should test for every monday and wednesday")
    @Test
    void it_should_test_for_every_monday_and_wednesday() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.days(1, 3).build();
        assertEquals("* * * * 1,3", expression);
    }

    @DisplayName("it should test between monday and friday")
    @Test
    void it_should_test_between_monday_and_friday() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.days("Mon-Fri").build();
        assertEquals("* * * * Mon-Fri", expression);
    }

    @DisplayName("it should test hourly at")
    @Test
    void it_should_test_hourly_at() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.hourlyAt(1, 6, 12).build();
        assertEquals("* 1,6,12 * * *", expression);
    }

    @DisplayName("it should test daily at")
    @Test
    void it_should_test_daily_at() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.dailyAt("15:30").build();
        assertEquals("30 15 * * *", expression);
    }

    @DisplayName("it should test weekly on")
    @Test
    void it_should_test_weekly_on() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.weeklyOn("Mon", "19:45").build();
        assertEquals("45 19 * * Mon", expression);
    }

    @DisplayName("it should test monthly on")
    @Test
    void it_should_test_monthly_on() {
        final ScheduleBuilder builder = new ScheduleBuilder();
        final String expression = builder.monthlyOn(15, "21:15").build();
        assertEquals("15 21 15 * *", expression);
    }

}