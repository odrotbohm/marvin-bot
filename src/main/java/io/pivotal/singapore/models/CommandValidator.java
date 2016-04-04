package io.pivotal.singapore.models;

import io.pivotal.singapore.marvin.commands.arguments.Argument;
import io.pivotal.singapore.marvin.commands.arguments.Arguments;
import io.pivotal.singapore.marvin.commands.arguments.InvalidArgument;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

public class CommandValidator implements Validator {
    /**
     * This Validator validates *just* Person instances
     */
    public boolean supports(Class clazz) {
        return Command.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {
        Command command = (Command) obj;
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "name", "name.empty");

        if (command.getName() != null && command.getName().contains(" ")) {
            e.rejectValue("name", "name.nospaces", "Name can't contain spaces");
        }


        List<SubCommand> subCommand = command.getSubCommands();
        int i = 0;
        for (SubCommand cmd : subCommand) {
            Arguments arguments = cmd.getArguments();
            int j = 0;
            for (Argument arg : arguments.getArguments()) {
                if (arg instanceof InvalidArgument) {
                    e.rejectValue(
                        String.format("subCommands[%d].arguments[%d]", i, j),
                        null,
                        String.format("The argument '%s' is an invalid type", arg.getName())
                    );
                }
                j++;
            }
            i++;
        }
    }
}
