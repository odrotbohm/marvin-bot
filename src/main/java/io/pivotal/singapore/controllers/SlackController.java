package io.pivotal.singapore.controllers;

import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.repositories.CommandRepository;
import io.pivotal.singapore.services.RemoteApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.composed.web.rest.json.GetJson;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    private Clock clock = Clock.systemUTC();
    
    @GetJson("/")
    public Map<String, String> index(@RequestParam HashMap<String, String> params) {
        String commandText = params.get("text");

        if (commandText == null || commandText.isEmpty()) {
            return defaultResponse();
        }

        Optional<Command> commandOptional = getCommand(commandText);
        if (!commandOptional.isPresent()) {
            return defaultResponse();
        }

        HashMap<String, String> response = remoteApiService.call(commandOptional.get(), remoteServiceParams(params));
        return textResponse(response.get("message"));
    }

    private HashMap<String, String> remoteServiceParams(HashMap<String, String> params) {
        HashMap<String, String> serviceParams = new HashMap<>();
        serviceParams.put("user", String.format("%s@pivotal.io", params.get("user_name")));
        serviceParams.put("channel", params.get("channel_name"));
        serviceParams.put("received_at", ZonedDateTime.now(clock).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        serviceParams.put("command", params.get("text"));

        return serviceParams;
    }

    private Optional<Command> getCommand(String commandText) {
        Integer firstSpace = commandText.indexOf(' ');
        String commandName = firstSpace == -1 ? commandText : commandText.substring(0, firstSpace);

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
