package io.pivotal.singapore.services;

import io.pivotal.singapore.models.Command;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CommandServiceTest {

   private CommandService subject = new CommandService();

    @Test
    public void getCommands() {
        List<Command> commands = subject.getCommands();
        assertThat(commands.size(), is(0));
    }

    @Test
    public void setCommands() {
        Command command = new Command("", "");
        subject.addCommand(command);
        assertThat(subject.getCommands().get(0) , is(command));
    }


}
