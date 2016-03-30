package io.pivotal.singapore.models;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

public interface ICommand {
    String getName();
    String getEndpoint();
    RequestMethod getMethod();

    String getDefaultResponseSuccess();
    String getDefaultResponseFailure();

    List<Map<String, String>> getArguments();
}
