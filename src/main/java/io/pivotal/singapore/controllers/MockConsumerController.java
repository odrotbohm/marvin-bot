package io.pivotal.singapore.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MockConsumerController {

    @RequestMapping(value = "/mocker", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> index(@RequestBody Map<String, Object> params) {

        Map<String, String> arguments = (Map) params.get("arguments");

        String location = arguments.get("location");
        String time;
        if(location.equals("England")) time = "It is Tea 'o' Clock";
        else if(location.equals("Australia")) time = "It's Beer 'o' Clock";
        else if(location.equals("Russia")) time = "It'z Vodka 'o' Clock";

        else time = "Its " + new Date();

        return Collections.singletonMap("message", time);
    }
}
