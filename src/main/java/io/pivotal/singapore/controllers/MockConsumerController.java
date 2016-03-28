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
    public Map<String, String> index(@RequestBody Map<String, Object> params) {

        Map<String, String> arguments = (Map) params.get("arguments");
        String response_message = "Its " + new Date();

        if(arguments != null) {
            String location = arguments.get("location");

            if(location.equals("England")) {
                response_message = "It is Tea 'o' Clock";
            }
            else if(location.equals("Australia")) {
                response_message = "It's Beer 'o' Clock";
            }
            else if(location.equals("Russia")) {
                response_message = "It'z Vodka 'o' Clock";
            }
        }

        HashMap<String,String> response = new HashMap<String, String>();
        response.put("message", response_message);
        response.put("message_type", "channel");

        return response;
    }
}
