package io.pivotal.singapore.services;

import io.pivotal.singapore.models.Command;
import org.junit.After;
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

    private HashMap<String, String> callRemoteApiService() {
        return remoteApiService.call(command.getMethod(), command.getEndpoint(), params);
    }

    private void setupMockServer(String endpoint, HttpMethod method) {
        mockServer.expect(requestTo(endpoint))
                .andExpect(method(method))
                .andRespond(withSuccess("{ \"status\" : \"SUCCESS!!!!!!!\" }", MediaType.APPLICATION_JSON));
    }

    @Before
    public void setUp() throws Exception {
        restTemplate = new RestTemplate();
        remoteApiService = new RemoteApiService(restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);

        command = new Command("some command", "http://example.com/");
        params = new HashMap<>();
        params.put("rawCommand", "time location Singapore");
    }

    @After
    public void tearDown() throws Exception {
        mockServer.verify();
    }

    @Test
    public void callsEndpointWithPost() throws Exception {
        command.setMethod(RequestMethod.POST);
        setupMockServer("http://example.com/", HttpMethod.POST);

        HashMap<String, String> result = callRemoteApiService();

        assertThat(result.get("status"), is(equalTo("SUCCESS!!!!!!!")));
    }

    @Test
    public void callsEndpointWithGet() throws Exception {
        command.setMethod(RequestMethod.GET);
        setupMockServer("http://example.com/?rawCommand=time%20location%20Singapore", HttpMethod.GET);

        HashMap<String, String> result = callRemoteApiService();

        assertThat(result.get("status"), is(equalTo("SUCCESS!!!!!!!")));
    }

    @Test
    public void callsEndpointWithPut() throws Exception {
        command.setMethod(RequestMethod.PUT);
        setupMockServer("http://example.com/", HttpMethod.PUT);

        HashMap<String, String> result = callRemoteApiService();

        assertThat(result.get("status"), is(equalTo("SUCCESS!!!!!!!")));
    }

    @Test
    public void callsEndpointWithDelete() throws Exception {
        command.setMethod(RequestMethod.DELETE);
        setupMockServer("http://example.com/", HttpMethod.DELETE);

        HashMap<String, String> result = callRemoteApiService();

        assertThat(result.get("status"), is(equalTo("SUCCESS!!!!!!!")));
    }
}
