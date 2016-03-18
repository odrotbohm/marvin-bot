package io.pivotal.singapore.services;

import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.repositories.CommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandService {

    @Autowired
    CommandRepository cmdRepository;

    public List<Command> getCommands() {
        return cmdRepository.findAll();
    }

    public void addCommand(Command command) {
        cmdRepository.save(command);
    }
}
