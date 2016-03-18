package io.pivotal.singapore.models;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CommandTest {

    @Test
    public void constructorTest() {
        Command command = new Command("name", "endpoint");
        assertThat(command.getName(), is("name"));
        assertThat(command.getEndpoint(), is("endpoint"));
    }

}
