package io.pivotal.singapore.services;

import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.models.SubCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
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

    public HashMap<String, String> call(RequestMethod method, String endpoint, Map params) {
        switch (method) {
            case POST:
                return restTemplate.postForObject(endpoint, params, HashMap.class);
            default:
                throw new IllegalArgumentException(
                    String.format("HTTP method '%s' not supported.", method)
                );
        }
    }
}
