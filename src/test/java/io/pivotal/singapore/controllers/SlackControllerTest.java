package io.pivotal.singapore.controllers;

import io.pivotal.singapore.MarvinApplication;
import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.models.SubCommand;
import io.pivotal.singapore.repositories.CommandRepository;
import io.pivotal.singapore.services.CommandParserService;
import io.pivotal.singapore.services.RemoteApiService;
import io.pivotal.singapore.utils.FrozenTimeMachine;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

        @Spy
        private FrozenTimeMachine clock;

        @Spy
        private CommandParserService commandParserService;

        @InjectMocks
        private SlackController controller;

        private HashMap<String, String> slackInputParams;

        private Command command;
        private Optional<Command> optionalCommand;

        private Map<String, String> response;
        private Map<String, String> apiServiceParams;

        @Before
        public void setUp() {
            command = new Command("time", "http://somesuch.tld/api/time/");
            optionalCommand = Optional.of(command);

            slackInputParams = new HashMap<>();
            slackInputParams.put("token", "gIkuvaNzQIHg97ATvDxqgjtO");
            slackInputParams.put("team_id", "T0001");
            slackInputParams.put("team_domain", "example");
            slackInputParams.put("channel_id", "C2147483705");
            slackInputParams.put("channel_name", "test");
            slackInputParams.put("user_id", "U2147483697");
            slackInputParams.put("user_name", "Steve");
            slackInputParams.put("command", "/marvin");
            slackInputParams.put("text", "time");
            slackInputParams.put("response_url", "https://hooks.slack.com/commands/1234/5678");

            apiServiceParams = new HashMap<>();
            apiServiceParams.put("user", "Steve@pivotal.io");
            apiServiceParams.put("channel", "test");
            apiServiceParams.put("received_at", ZonedDateTime.now(clock).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
            apiServiceParams.put("command", "time");

            response = new HashMap<>();
            response.put("response_type", "ephemeral");
            response.put("text", "This will all end in tears.");
        }

        @Test
        public void testReceiveTimeOfDay() {
            when(commandRepository.findOneByName("time")).thenReturn(Optional.empty());

            assertThat(controller.index(slackInputParams), is(equalTo(response)));
        }

        @Test
        public void findsCommandAndCallsEndpoint() {
            when(commandRepository.findOneByName("time")).thenReturn(optionalCommand);

            HashMap<String, String> serviceResponse = new HashMap<>();
            String australiaTime = "The time in Australia is Beer o'clock.";
            serviceResponse.put("message", australiaTime);
            when(remoteApiService.call(command.getMethod(), command.getEndpoint(), apiServiceParams)).thenReturn(serviceResponse);

            Map<String, String> response = controller.index(slackInputParams);
            assertThat(response.get("text"), is(equalTo(australiaTime)));
            verify(commandRepository, times(1)).findOneByName("time");
            verify(remoteApiService, times(1)).call(command.getMethod(), command.getEndpoint(), apiServiceParams);
        }

        @Test
        public void findsCommandWhenItHasArguments() {
            slackInputParams.put("text", "time england");
            apiServiceParams.put("command", "time england");

            when(commandRepository.findOneByName("time")).thenReturn(optionalCommand);

            HashMap<String, String> serviceResponse = new HashMap<>();
            String englandTime = "The time in England is Tea o'clock.";
            serviceResponse.put("message", englandTime);
            when(remoteApiService.call(command.getMethod(), command.getEndpoint(), apiServiceParams)).thenReturn(serviceResponse);

            Map<String, String> response = controller.index(slackInputParams);
            assertThat(response.get("text"), is(equalTo(englandTime)));
        }

        @Test
        public void findsSubCommandsWhenItHasThem() {
            slackInputParams.put("text", "time in London");
            apiServiceParams.put("command", "time in London");

            SubCommand subCommand = new SubCommand();
            subCommand.setName("in");

            when(commandRepository.findOneByName("time")).thenReturn(optionalCommand);

            List<SubCommand> subCommands = new ArrayList<>();
            subCommands.add(subCommand);
            command.setSubCommands(subCommands);
            Map<String, String> response = controller.index(slackInputParams);
            verify(remoteApiService).call(subCommand.getMethod(), subCommand.getEndpoint(), apiServiceParams);
        }
    }

}
