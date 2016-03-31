package io.pivotal.singapore.controllers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class EchoConsumerControllerTest {

    @InjectMocks
    private EchoConsumerController controller;

    @Test
    public void commandHandlerReturnsArgumentsAndMethod() throws IOException {
        final ImmutableMap<String, Object> params =
                new ImmutableMap.Builder<String, Object>()
                        .put("arguments", new ImmutableMap.Builder<String, Integer>()
                                .put("a", 1).build()).build();

        Map<String, String> actual = controller.handleCommand(params, HttpMethod.DELETE);
        Map<String, String> message = jsonToMap(actual.get("message"));
        Map<String, String> arguments = jsonToMap(message.get("arguments"));

        assertThat(message, hasEntry("method", "DELETE"));
        assertThat(arguments, hasEntry("a", 1));
        assertThat(actual, hasEntry("message_type", "channel"));
    }

    @Test
    public void subCommandHandlerReturnsSubCommand() throws IOException {
        final ImmutableMap<String, Object> params =
                new ImmutableMap.Builder<String, Object>()
                        .put("arguments", new ImmutableMap.Builder<String, Integer>()
                                .put("a", 1).build()).build();

        Map<String, String> actual = controller.handleSubcommand(params, HttpMethod.DELETE, "sub_command");
        Map<String, String> message = jsonToMap(actual.get("message"));
        Map<String, String> arguments = jsonToMap(message.get("arguments"));

        assertThat(message,
                allOf(hasEntry("method", "DELETE"),
                        hasEntry("subCommand", "sub_command")));
        assertThat(arguments, hasEntry("a", 1));
        assertThat(actual, hasEntry("message_type", "channel"));
    }

    private Map jsonToMap(String mes) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(mes, Map.class);
    }
}