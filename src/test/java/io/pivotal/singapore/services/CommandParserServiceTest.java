package io.pivotal.singapore.services;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CommandParserServiceTest {
    CommandParserService service = new CommandParserService();

    @Test
    public void testParseWhenAllTokensArePresent() {
        String textCommand = "event create \"BBQ at Pivotal labs\" at 7pm on Tuesday";

        HashMap<String, String> result = service.parse(textCommand);

        assertThat(result.get("command"), is("event"));
        assertThat(result.get("sub_command"), is("create"));
        assertThat(result.get("arguments"), is("\"BBQ at Pivotal labs\" at 7pm on Tuesday"));
    }

    @Test
    public void testParseWhenCommandIsMissing() {
        String textCommand = "";

        HashMap<String, String> result = service.parse(textCommand);

        assertThat(result.get("command"), is(nullValue()));
        assertThat(result.get("sub_command"), is(nullValue()));
        assertThat(result.get("arguments"), is(nullValue()));
    }

    @Test
    public void testParseWhenSubCommandIsMissing() {
        String textCommand = "event";

        HashMap<String, String> result = service.parse(textCommand);

        assertThat(result.get("command"), is("event"));
        assertThat(result.get("sub_command"), is(nullValue()));
        assertThat(result.get("arguments"), is(nullValue()));
    }

    @Test
    public void testParseWhenArgumentsAreMissing() {
        String textCommand = "event create";

        HashMap<String, String> result = service.parse(textCommand);

        assertThat(result.get("command"), is("event"));
        assertThat(result.get("sub_command"), is("create"));
        assertThat(result.get("arguments"), is(nullValue()));
    }
}