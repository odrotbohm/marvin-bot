package io.pivotal.singapore.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class ArgumentParserServiceTest {

    private ArgumentParserService aps = new ArgumentParserService();
    private LocalDateTime expectedDateTime;

    @Before
    public void setup() {
        expectedDateTime = LocalDateTime.of(LocalDate.of(2016, 3, 23), LocalTime.of(19, 0));
    }

    @Test
    public void parseTimestringOnItsOwn() {
        Optional actual = aps.parseTimestamp("23rd of March at 7pm");

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), equalTo(expectedDateTime));
    }

    @Test
    public void noValidTimeString() {
        Optional actual = aps.parseTimestamp("I'm a fluffy ballonicorn!");

        assertThat(actual.isPresent(), is(false));
    }

    @Test
    public void parseValidTimeStringWithOtherStuff() {
        Optional actual = aps.parseTimestamp("BBQ At the Pivotal Labs Singapore office on the 23rd of March at 7pm");

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), equalTo(expectedDateTime));
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
            assertThat(aps.parseTimestamp(validString).isPresent(), is(true));
        }
    }
}
