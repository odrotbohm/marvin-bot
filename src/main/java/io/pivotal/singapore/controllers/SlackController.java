package io.pivotal.singapore.controllers;

import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.repositories.CommandRepository;
import io.pivotal.singapore.services.RemoteApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class SlackController {
    @Autowired
    CommandRepository commandRepository;

    @Autowired
    RemoteApiService remoteApiService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, String> index(@RequestParam HashMap<String, String> params) {
        String commandText = params.get("text");

        if (commandText.isEmpty()) {
            return defaultResponse();
        }

        Integer firstSpace = commandText.indexOf(' ');
        String commandName = firstSpace == -1 ? commandText : commandText.substring(0, firstSpace);
        Optional<Command> commandOptional = commandRepository.findOneByName(commandName);

        if (!commandOptional.isPresent()) {
            return defaultResponse();
        }

        HashMap<String, String> response = remoteApiService.call(commandOptional.get(), params);


        return textResponse(response.get("status"));
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
