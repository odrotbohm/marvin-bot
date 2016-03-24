package io.pivotal.singapore.services;

import io.pivotal.singapore.utils.ThrowableCatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static java.util.Collections.singletonMap;

@RunWith(Enclosed.class)
public class ArgumentParserServiceTest {
    private static class BaseTest {

        ArgumentParserService aps = new ArgumentParserService();
        List<Map<String, String>> argumentsConfig;
        String expectedDateTimeString;
        ZonedDateTime expectedDateTime;

        @Before
        public void setup() {
            expectedDateTime = ZonedDateTime.of(LocalDate.of(2016, 3, 23), LocalTime.of(19, 0), ZoneId.of("+08:00"));
            expectedDateTimeString = expectedDateTime.format(ISO_OFFSET_DATE_TIME);

            argumentsConfig = new ArrayList<Map<String, String>>();
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class TimeParsing extends BaseTest {
        @Before
        public void setUp() {
            argumentsConfig.add(singletonMap("timestamp", "TIMESTAMP"));
        }

        @Test
        public void parseTimestringOnItsOwn() {
            Map actual = aps.parse("23rd of March at 7pm", argumentsConfig);

            assertThat(actual.get("timestamp"), equalTo(expectedDateTimeString));
        }

        @Test(expected = IllegalArgumentException.class)
        public void noValidTimeString() {
            Map actual = aps.parse("I'm a fluffy ballonicorn!", argumentsConfig);

            actual.get("timestamp");
        }

        @Test
        public void parseValidTimeStringWithOtherStuff() {
            Map actual = aps.parse("BBQ At the Pivotal Labs Singapore office on the 23rd of March at 7pm", argumentsConfig);

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
                Throwable actual = ThrowableCatcher.capture(() -> aps.parse(validString, argumentsConfig));
                assertThat(actual, is(nullValue()));
            }
        }

        @Test
        public void parseArgumentsShouldBeEvaluatedInOrder() {
            List <Map<String, String>> argumentsConfig = new ArrayList<Map<String, String>>();

            String s = "23rd of March at 7pm \"BBQ At the Pivotal Labs Singapore office\"";

            argumentsConfig.add(singletonMap("start_time", "TIMESTAMP"));
            argumentsConfig.add(singletonMap("event_name", "/\"([^\"]+)\"/"));

            Map result = aps.parse(s, argumentsConfig);

            assertThat(result.get("start_time"), equalTo(expectedDateTimeString));
            assertThat(result.get("event_name"), equalTo("BBQ At the Pivotal Labs Singapore office"));
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class ParseArgument extends BaseTest {
        @Test
        public void parseStringWithArguments() {
            String s = "\"BBQ At the Pivotal Labs Singapore office\" on the 23rd of March at 7pm";
            argumentsConfig.add(singletonMap("event_name", "/\"([^\"]+)/"));
            argumentsConfig.add(singletonMap("start_time", "TIMESTAMP"));

            Map result = aps.parse(s, argumentsConfig);

            assertThat(result.get("event_name"), equalTo("BBQ At the Pivotal Labs Singapore office"));
            assertThat(result.get("start_time"), equalTo(expectedDateTimeString));
        }

        @Test
        public void parseRemovesAllCharactersOfMatchedGroup() {
            String s = "\"BBQ At the Pivotal Labs Singapore office\" on the 23rd of March at 7pm";
            argumentsConfig.add(singletonMap("event_name", "/\"([^\"]+)\"/"));
            argumentsConfig.add(singletonMap("the_on", "/(on )/"));
            argumentsConfig.add(singletonMap("start_time", "TIMESTAMP"));

            Map result = aps.parse(s, argumentsConfig);

            assertThat(result.get("event_name"), equalTo("BBQ At the Pivotal Labs Singapore office"));
            assertThat(result.get("the_on"), equalTo("on "));
            assertThat(result.get("start_time"), equalTo(expectedDateTimeString));
        }

        @Test(expected = IllegalArgumentException.class)
        public void parseStringWithInvalidArgumentsRaisesException() {
            argumentsConfig.add(singletonMap("event_name", "m000"));

            aps.parse("", argumentsConfig);
        }

        @Test(expected = IllegalArgumentException.class)
        public void raisesExceptionWhenAPartDoesntMatch() {
            String s = "Hello 123 there";
            argumentsConfig.add(singletonMap("first", "/\\w+/"));
            argumentsConfig.add(singletonMap("second", "/\\w+/"));

            aps.parse(s, argumentsConfig);
        }

        @Test(expected = IllegalArgumentException.class)
        public void ensureArgumentIsMatchedFromBeginningOfString() {
            String s = "I18N InternationalizatioN";
            argumentsConfig.add(singletonMap("first", "/(I.{18}N)/"));

            aps.parse(s, argumentsConfig);
        }

        @Test
        public void ensureLineIsTrimmedFromLeadingAndTrailingWhitespace() {
            String s = "     Hello   There    ";
            argumentsConfig.add(singletonMap("first", "/(Hello)/"));
            argumentsConfig.add(singletonMap("second", "/(There)/"));

            Map result = aps.parse(s, argumentsConfig);

            assertThat(result.get("first"), equalTo("Hello"));
            assertThat(result.get("second"), equalTo("There"));
        }
    }
}
