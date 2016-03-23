package io.pivotal.singapore.services;

import io.pivotal.singapore.models.Command;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoteApiService {
    private RestTemplate restTemplate;

    public RemoteApiService() {
        restTemplate = new RestTemplate();
    }

    public RemoteApiService(RestTemplate _restTemplate) {
        restTemplate = _restTemplate;
    }

    public HashMap<String, String> call(Command command, Map params) {
        switch (command.getMethod()) {
            case POST:
                return restTemplate.postForObject(command.getEndpoint(), params, HashMap.class);
            default:
                throw new IllegalArgumentException(
                    String.format("HTTP method '%s' not supported.", command.getMethod())
                );
        }
    }
}
