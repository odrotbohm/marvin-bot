package io.pivotal.singapore.controllers;

import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.models.SubCommand;
import io.pivotal.singapore.repositories.CommandRepository;
import io.pivotal.singapore.services.CommandParserService;
import io.pivotal.singapore.services.RemoteApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.composed.web.rest.json.GetJson;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class SlackController {
    @Autowired
    CommandRepository commandRepository;

    @Autowired
    RemoteApiService remoteApiService;

    @Autowired
    CommandParserService commandParserService;

    private Clock clock = Clock.systemUTC();

    @GetJson("/")
    public Map<String, String> index(@RequestParam HashMap<String, String> params) {
        String commandText = params.get("text");

        if (commandText == null || commandText.isEmpty()) {
            return defaultResponse();
        }

        HashMap<String, String> parsedCommand = commandParserService.parse(commandText);

        Optional<Command> commandOptional = getCommand(parsedCommand.get("command"));
        if (!commandOptional.isPresent()) {
            return defaultResponse();
        }

        HashMap<String, String> response;
        Optional<SubCommand> subCommandOptional = getSubCommand(commandOptional, parsedCommand.get("sub_command"));
        if (!subCommandOptional.isPresent()) {
            Command command = commandOptional.get();
            response = remoteApiService.call(command.getMethod(), command.getEndpoint(), remoteServiceParams(params));
        } else {
            SubCommand subCommand = subCommandOptional.get();
            response = remoteApiService.call(subCommand.getMethod(), subCommand.getEndpoint(), remoteServiceParams(params));
        }

        return textResponse(response.get("message"));
    }

    private Optional<SubCommand> getSubCommand(Optional<Command> commandOptional, String subCommandText) {
        return commandOptional.flatMap((c)-> c.findSubCommand(subCommandText));
    }

    private HashMap<String, String> remoteServiceParams(HashMap<String, String> params) {
        HashMap<String, String> serviceParams = new HashMap<>();
        serviceParams.put("user", String.format("%s@pivotal.io", params.get("user_name")));
        serviceParams.put("channel", params.get("channel_name"));
        serviceParams.put("received_at", ZonedDateTime.now(clock).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        serviceParams.put("command", params.get("text"));

        return serviceParams;
    }

    private Optional<Command> getCommand(String commandName) {
        return commandRepository.findOneByName(commandName);
    }

    private HashMap<String, String> textResponse(String text) {
        HashMap<String, String> response = new HashMap<>();
        response.put("response_type", "ephemeral");
        response.put("text", text);

        return response;
    }

    private HashMap<String, String> defaultResponse() {
        return textResponse("This will all end in tears.");
    }
}
