package io.pivotal.singapore.services;

import io.pivotal.singapore.models.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class RemoteApiServiceTest {
    private RemoteApiService remoteApiService;
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private Command command;
    private HashMap<String, String> params;

    @Before
    public void setUp() throws Exception {
        restTemplate = new RestTemplate();
        remoteApiService = new RemoteApiService(restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);

        command = new Command("some command", "http://example.com/");
        params = new HashMap<>();
        params.put("rawCommand", "time location Singapore");
    }

    @Test
    public void callsEndpointWithPost() throws Exception {
        mockServer.expect(requestTo("http://example.com/"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{ \"status\" : \"SUCCESS!!!!!!!\" }", MediaType.APPLICATION_JSON));

        HashMap <String, String> result = remoteApiService.call(command.getMethod(), command.getEndpoint(), params);

        assertThat(result.get("status"), is(equalTo("SUCCESS!!!!!!!")));
        mockServer.verify();
    }

    @Test
    public void callsEndpointWithGet() throws Exception {
        command.setMethod(RequestMethod.GET);

        mockServer.expect(requestTo("http://example.com/?rawCommand=time%20location%20Singapore"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("{ \"time\": \"It is Tiger time\" }", MediaType.APPLICATION_JSON));

        HashMap <String, String> result = remoteApiService.call(command.getMethod(), command.getEndpoint(), params);

        assertThat(result.get("time"), is(equalTo("It is Tiger time")));
        mockServer.verify();
    }

    @Test
    public void callsEndpointWithPut() throws Exception {
        command.setMethod(RequestMethod.PUT);

        mockServer.expect(requestTo("http://example.com/"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess("{ \"status\" : \"SUCCESS!!!!!!!\" }", MediaType.APPLICATION_JSON));

        HashMap <String, String> result = remoteApiService.call(command.getMethod(), command.getEndpoint(), params);

        assertThat(result.get("status"), is(equalTo("SUCCESS!!!!!!!")));
        mockServer.verify();
    }

    @Test
    public void callsEndpointWithDelete() throws Exception {
        command.setMethod(RequestMethod.DELETE);

        mockServer.expect(requestTo("http://example.com/"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("{ \"status\" : \"SUCCESS!!!!!!!\" }", MediaType.APPLICATION_JSON));

        HashMap <String, String> result = remoteApiService.call(command.getMethod(), command.getEndpoint(), params);

        assertThat(result.get("status"), is(equalTo("SUCCESS!!!!!!!")));
        mockServer.verify();
    }
}
