package io.pivotal.singapore.controllers;

import io.pivotal.singapore.MarvinApplication;
import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.repositories.CommandRepository;
import io.pivotal.singapore.services.RemoteApiService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Enclosed.class)
public class SlackControllerTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = MarvinApplication.class)
    @WebAppConfiguration
    @ActiveProfiles(profiles = "test")
    public static class Integration {
        @Autowired
        private WebApplicationContext wac;

        private MockMvc mockMvc;

        @Before
        public void setUp() throws Exception {
            mockMvc = webAppContextSetup(wac).build();
        }

        @Test
        public void testHelloWorld() throws Exception {
            mockMvc.perform(get("/")
                .param("text", "")
                .param("team_id", "pivotal.io")
                .param("user_name", "bandersson")
            ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{\"response_type\":\"ephemeral\",\"text\":\"This will all end in tears.\"}"));
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class CommandTesting {
        @Mock
        private CommandRepository commandRepository;

        @Mock
        private RemoteApiService remoteApiService;

        @InjectMocks
        private SlackController controller;

        private HashMap<String, String> params;

        private Command command;
        private Optional<Command> optionalCommand;

        private Map<String, String> defaultResponse;

        @Before
        public void setUp() {
            command = new Command("time", "http://somesuch.tld/api/time/");
            optionalCommand = Optional.of(command);

            params = new HashMap<>();
            params.put("token", "gIkuvaNzQIHg97ATvDxqgjtO");
            params.put("team_id", "T0001");
            params.put("team_domain", "example");
            params.put("channel_id", "C2147483705");
            params.put("channel_name", "test");
            params.put("user_id", "U2147483697");
            params.put("user_name", "Steve");
            params.put("command", "/marvin");
            params.put("text", "time");
            params.put("response_url", "https://hooks.slack.com/commands/1234/5678");

            defaultResponse = new HashMap<>();
            defaultResponse.put("response_type", "ephemeral");
            defaultResponse.put("text", "This will all end in tears.");
        }

        @Test
        public void testReceiveTimeOfDay() {
            when(commandRepository.findOneByName("time")).thenReturn(Optional.empty());

            assertThat(controller.index(params), is(equalTo(defaultResponse)));
        }

        @Test
        public void findsCommandAndCallsEndpoint() {
            when(commandRepository.findOneByName("time")).thenReturn(optionalCommand);

            HashMap<String, String> serviceResponse = new HashMap<>();
            String australiaTime = "The time in Australia is Beer o'clock.";
            serviceResponse.put("status", australiaTime);
            when(remoteApiService.call(command, params)).thenReturn(serviceResponse);

            Map<String, String> response = controller.index(params);
            assertThat(response.get("text"), is(equalTo(australiaTime)));
            verify(commandRepository, times(1)).findOneByName("time");
            verify(remoteApiService, times(1)).call(command, params);
        }

        @Test
        public void findsCommandWhenItHasArguments() {
            params.put("text", "time england");

            when(commandRepository.findOneByName("time")).thenReturn(optionalCommand);

            HashMap<String, String> serviceResponse = new HashMap<>();
            String englandTime = "The time in England is Tea o'clock.";
            serviceResponse.put("status", englandTime);
            when(remoteApiService.call(command, params)).thenReturn(serviceResponse);

            Map<String, String> response = controller.index(params);
            assertThat(response.get("text"), is(equalTo(englandTime)));
            verify(commandRepository, times(1)).findOneByName("time");
            verify(remoteApiService, times(1)).call(command, params);
        }
    }

}
