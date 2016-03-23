package io.pivotal.singapore.integrations.api;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import io.pivotal.singapore.MarvinApplication;
import io.pivotal.singapore.models.Command;
import io.pivotal.singapore.repositories.CommandRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MarvinApplication.class)
@WebAppConfiguration
@ActiveProfiles(profiles = "test")
@IntegrationTest("server.port:0")

public class CommandResourceTest {
    @Autowired private WebApplicationContext wac;
    @Autowired private CommandRepository commandRepository;

    @Value("${local.server.port}")
    protected int port;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
    }

    @After
    public void tearDown() {
        commandRepository.deleteAll();
    }

    @Test
    public void testCommandsListing() throws Exception {
        commandRepository.save(new Command("cmd1", "http://localhost/1"));
        commandRepository.save(new Command("cmd2", "http://localhost/2"));

        when().
                get("/api/v1/commands").
        then().
                statusCode(SC_OK).
                contentType(MediaTypes.HAL_JSON_VALUE).
                body("_embedded.commands.size()", is(2)).
                body("_embedded.commands[0].name", is("cmd1")).
                body("_embedded.commands[0].endpoint", is("http://localhost/1")).
                body("_embedded.commands[1].name", is("cmd2")).
                body("_embedded.commands[1].endpoint", is("http://localhost/2"));
    }

    @Test
    public void testCommandsCreation() throws Exception {
        String json = new JSONObject()
                .put("name", "command")
                .put("endpoint", "http://localhost/9")
                .put("method", "GET")
                .toString();

        given().
                contentType(ContentType.JSON).
                content(json).
        when().
                post("/api/v1/commands/").
        then().
                statusCode(SC_CREATED);

        when().
                get("/api/v1/commands").
        then().
                body("_embedded.commands.size()", is(1)).
                body("_embedded.commands[0].name", is("command")).
                body("_embedded.commands[0].endpoint", is("http://localhost/9"));
    }

    @Test
    public void testCommandsUpdateViaName() throws Exception {
        JSONObject originalJson = new JSONObject()
                .put("name", "foobar")
                .put("endpoint", "http://localhost/9")
                .put("method", "GET");

        given().
                contentType(ContentType.JSON).
                content(originalJson.toString()).
        when().
                post("/api/v1/commands/").
        then().
                statusCode(SC_CREATED);

        JSONObject newJson = originalJson.put("method", "POST");

        given().
                contentType(ContentType.JSON).
                content(newJson.toString()).
        when().
                post("/api/v1/commands/").
        then().
                statusCode(SC_CREATED);

        when().
                get("/api/v1/commands/").
        then().
                content("page.totalElements", equalTo(1)).
                body("_embedded.commands[0].name", is("foobar")).
                body("_embedded.commands[0].endpoint", is("http://localhost/9")).
                body("_embedded.commands[0].method", is("POST"));
    }

    @Test
    public void testCommandCreationWithSubCommands() {
        JSONArray subCommands = new JSONArray();

        HashMap<String, String> args = new HashMap<>();
        args.put("arg1", "form1");
        JSONObject subCommandObject = new JSONObject()
                .put("name", "bar")
                .put("endpoint", "/bar")
                .put("method", "POST")
                .put("arguments", args);

        subCommands.put(subCommandObject);


        JSONObject json = new JSONObject()
                .put("name", "pity the fool")
                .put("endpoint", "http://localhost/9")
                .put("method", "GET")
                .put("subCommands", subCommands);

        given().
                log().all().
                contentType(ContentType.JSON).
                content(json.toString()).
        when().
                log().all().
                post("/api/v1/commands/").
        then().
                log().all().
                statusCode(SC_CREATED).
                body("subCommands[0].name", is("bar")).
                body("subCommands[0].method", is("POST")).
                body("subCommands[0].endpoint", is("/bar")).
                body("subCommands[0].arguments.arg1", is("form1"));
    }
}
