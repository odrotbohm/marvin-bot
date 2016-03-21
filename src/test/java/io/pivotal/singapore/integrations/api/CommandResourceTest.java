package io.pivotal.singapore.integrations.api;

import io.pivotal.singapore.MarvinApplication;
import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.services.CommandService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MarvinApplication.class)
@WebAppConfiguration
@ActiveProfiles(profiles = "test")
public class CommandResourceTest {
    @Autowired private WebApplicationContext wac;
    @Autowired private CommandService commandService;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac).build();

        commandService.addCommand(new Command("cmd1", "http://localhost/1"));
        commandService.addCommand(new Command("cmd2", "http://localhost/2"));
    }

    @Test
    @Transactional
    public void testCommandsListing() throws Exception {
        mockMvc.perform(get("/api/v1/commands"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.commands").isArray())
                .andExpect(jsonPath("$._embedded.commands.[0].name", is("cmd1")))
                .andExpect(jsonPath("$._embedded.commands.[0].endpoint", is("http://localhost/1")))
                .andExpect(jsonPath("$._embedded.commands.[1].name", is("cmd2")))
                .andExpect(jsonPath("$._embedded.commands.[1].endpoint", is("http://localhost/2")));
    }
}
