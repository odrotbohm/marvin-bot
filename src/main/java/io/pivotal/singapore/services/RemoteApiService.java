package io.pivotal.singapore.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
            case GET:
                return restTemplate.getForObject(buildUri(endpoint, params), HashMap.class);
            case PUT:
                return exchangeForObject(HttpMethod.PUT, endpoint, params);
            case DELETE:
                return exchangeForObject(HttpMethod.DELETE, endpoint, params);
            default:
                throw new IllegalArgumentException(
                    String.format("HTTP method '%s' not supported.", method)
                );
        }
    }

    private HashMap<String, String> exchangeForObject(HttpMethod method, String endpoint, Map params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);

        return restTemplate.exchange(endpoint, method, entity, HashMap.class).getBody();
    }

    private String buildUri(String endpoint, Map<String, String> arguments) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(endpoint);
        for( Map.Entry<String, String> arg : arguments.entrySet()) {
            builder = builder.queryParam(arg.getKey(), arg.getValue());
        }

        return builder.build().toUriString();

    }
}
