package io.pivotal.singapore.services;

import io.pivotal.singapore.models.Command;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommandService {

    private ArrayList<Command> commands = new ArrayList<Command>();

    public List<Command> getCommands() {
        return commands;
    }

    public void addCommand(Command command) {
        commands.add(command);
    }
}
