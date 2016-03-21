package io.pivotal.singapore.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.*;
import java.util.*;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(Enclosed.class)
public class ArgumentParserServiceTest {
    private static class BaseTest {

        ArgumentParserService aps = new ArgumentParserService();
        TreeMap<String, String> argumentsConfig;
        String expectedDateTimeString;
        ZonedDateTime expectedDateTime;

        @Before
        public void setup() {
            expectedDateTime = ZonedDateTime.of(LocalDate.of(2016, 3, 23), LocalTime.of(19, 0), ZoneId.of("+08:00"));
            expectedDateTimeString = expectedDateTime.format(ISO_OFFSET_DATE_TIME);

            argumentsConfig = new TreeMap<>();
            argumentsConfig.put("timestamp", "TIMESTAMP");
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class TimeParsing extends BaseTest {

        @Test
        public void parseTimestringOnItsOwn() {
            TreeMap actual = aps.parse("23rd of March at 7pm", argumentsConfig);

            assertThat(actual.get("timestamp"), equalTo(expectedDateTimeString));
        }

        @Test
        public void noValidTimeString() {
            TreeMap actual = aps.parse("I'm a fluffy ballonicorn!", argumentsConfig);

            assertThat(actual.get("timestamp") == null, is(true));
        }

        @Test
        public void parseValidTimeStringWithOtherStuff() {
            TreeMap actual = aps.parse("BBQ At the Pivotal Labs Singapore office on the 23rd of March at 7pm", argumentsConfig);

            assertThat(actual.get("timestamp"), equalTo(expectedDateTimeString));
        }

        @Test
        public void parseAllSupportedTimestrings() {
            List<String> validStrings = Arrays.asList(
                "on the 23rd of March at 7pm",
                "at 7pm on the 23rd of March",
                "23 March 7pm",
                "2016-03-23 7pm",
                "at 19:00 March 23",
                "next thursday at 9pm"
            );

            for (String validString : validStrings) {
                assertThat(aps.parse(validString, argumentsConfig).get("timestamp") != null, is(true));
            }
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class ParseArgument extends BaseTest {
        @Test
        public void parseStringWithArguments() {
            String s = "\"BBQ At the Pivotal Labs Singapore office\" on the 23rd of March at 7pm";
            TreeMap<String, String> argumentsConfig = new TreeMap<>();
            argumentsConfig.put("event_name", "/([^\"]+)/");
            argumentsConfig.put("start_time", "TIMESTAMP");

            TreeMap result = aps.parse(s, argumentsConfig);

            assertThat(result.get("event_name"), equalTo("BBQ At the Pivotal Labs Singapore office"));
            assertThat(result.get("start_time"), equalTo(expectedDateTimeString));
        }

        @Test(expected = IllegalArgumentException.class)
        public void parseStringWithInvalidArgumentsRaisesException() {
            TreeMap<String, String> argumentsConfig = new TreeMap<>();
            argumentsConfig.put("event_name", "m000");

            aps.parse("", argumentsConfig);
        }

        @Test(expected = IllegalArgumentException.class)
        public void raisesExceptionWhenAPartDoesntMatch() {
            String s = "Hello 123 there";
            TreeMap<String, String> arguments = new TreeMap<>();
            arguments.put("first", "/\\w+/");
            arguments.put("second", "/\\w+/");

            aps.parse(s, arguments);
        }

        @Test(expected = IllegalArgumentException.class)
        public void ensureArgumentIsMatchedFromBeginningOfString() {
            String s = "I18N InternationalizatioN";
            TreeMap<String, String> arguments = new TreeMap<>();
            arguments.put("first", "/(I.{18}N)/");

            aps.parse(s, arguments);
        }

    }
}
