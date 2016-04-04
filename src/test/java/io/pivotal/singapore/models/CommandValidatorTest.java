package io.pivotal.singapore.models;

import org.junit.Test;
import org.springframework.data.rest.core.ValidationErrors;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CommandValidatorTest {

    @Test
    public void checkCommandName() {
        Command command = new Command();

        SubCommand subCommand = new SubCommand();
        List<SubCommand> subCommands = Arrays.asList(subCommand);

        command.setSubCommands(subCommands);

        Errors errors = new BeanPropertyBindingResult(command, "command");
        CommandValidator subject = new CommandValidator();

        subject.validate(command, errors);

        assertThat(errors.getErrorCount(), is(equalTo(1)));
        assertThat(errors.getFieldError("name").getCode(), is(equalTo("name.empty")));

        errors = new BeanPropertyBindingResult(command, "command");
        command.setName("I pity the fool");
        subject.validate(command, errors);

        assertThat(errors.getErrorCount(), is(equalTo(1)));
        assertThat(errors.getFieldError("name").getCode(), is(equalTo("name.nospaces")));
    }

}
