package io.pivotal.singapore.controllers;

import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.models.ICommand;
import io.pivotal.singapore.models.SubCommand;
import io.pivotal.singapore.repositories.CommandRepository;
import io.pivotal.singapore.services.ArgumentParserService;
import io.pivotal.singapore.services.CommandParserService;
import io.pivotal.singapore.services.RemoteApiService;
import io.pivotal.singapore.utils.RemoteApiServiceResponse;
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

    @Autowired
    ArgumentParserService argumentParserService;

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

        RemoteApiServiceResponse response;
        Optional<SubCommand> subCommandOptional = getSubCommand(commandOptional, parsedCommand.get("sub_command"));
        Map _params = remoteServiceParams(params);
        String message;
        if (!subCommandOptional.isPresent()) {
            Command command = commandOptional.get();

            response = remoteApiService.call(command.getMethod(), command.getEndpoint(), _params);
            message = getMessage(response, command);
        } else {
            SubCommand subCommand = subCommandOptional.get();
            Map args = argumentParserService.parse(parsedCommand.get("arguments"), subCommand.getArguments());
            _params.put("arguments", args);

            response = remoteApiService.call(subCommand.getMethod(), subCommand.getEndpoint(), _params);
            message = getMessage(response, subCommand);
        }

        String messageType = response.getBody().get("message_type");
        return textResponse(messageType, message);
    }

    private String getMessage(RemoteApiServiceResponse response, ICommand command) {
        String message = response.getBody().get("message");

        if (message == null) {
            message = response.isSuccessful() ? command.getDefaultResponseSuccess() : command.getDefaultResponseFailure();

            if (message == null) { // No default message provided by service, so return whatever they sent
                message = response.getBody().toString();
            }
        }

        return message;
    }

    private Optional<SubCommand> getSubCommand(Optional<Command> commandOptional, String subCommandText) {
        return commandOptional.flatMap((c) -> c.findSubCommand(subCommandText));
    }

    private HashMap<String, Object> remoteServiceParams(HashMap<String, String> params) {
        HashMap<String, Object> serviceParams = new HashMap<>();
        serviceParams.put("username", String.format("%s@pivotal.io", params.get("user_name")));
        serviceParams.put("channel", params.get("channel_name"));
        serviceParams.put("received_at", ZonedDateTime.now(clock).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        serviceParams.put("command", params.get("text"));

        return serviceParams;
    }

    private Optional<Command> getCommand(String commandName) {
        return commandRepository.findOneByName(commandName);
    }

    HashMap<String, String> textResponse(String messageType, String text) {
        String responseType = nounMapping().get(messageType);

        if (responseType == null) {
            responseType = "ephemeral";
        }

        HashMap<String, String> response = new HashMap<>();
        response.put("response_type", responseType);
        response.put("text", text);

        return response;
    }

    private HashMap<String, String> nounMapping() {
        HashMap<String, String> nouns = new HashMap<>();
        nouns.put("user", "ephemeral");
        nouns.put("channel", "in_channel");

        return nouns;
    }

    private HashMap<String, String> defaultResponse() {
        return textResponse("user", "This will all end in tears.");
    }
}
