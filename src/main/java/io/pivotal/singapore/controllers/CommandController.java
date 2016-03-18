package io.pivotal.singapore.controllers;

import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.services.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/commands", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommandController {

    @Autowired private CommandService commandService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Command> index() {
        return commandService.getCommands();
    }

}
