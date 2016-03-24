package io.pivotal.singapore.services;

import io.pivotal.singapore.models.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
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

    @Before
    public void setUp() throws Exception {
        restTemplate = new RestTemplate();
        remoteApiService = new RemoteApiService(restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void callsEndpointWithTheCorrespondingMethod() throws Exception {
        Command command = new Command("some command", "http://example.com/");
        HashMap<String, String> params = new HashMap<>();
        params.put("rawCommand", "time location Singapore");

        mockServer.expect(requestTo("http://example.com/"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{ \"status\" : \"SUCCESS!!!!!!!\" }", MediaType.APPLICATION_JSON));
        HashMap <String, String> result = remoteApiService.call(command.getMethod(), command.getEndpoint(), params);

        assertThat(result.get("status"), is(equalTo("SUCCESS!!!!!!!")));
        mockServer.verify();
    }
}
