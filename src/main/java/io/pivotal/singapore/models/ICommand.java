package io.pivotal.singapore.models;

import org.springframework.web.bind.annotation.RequestMethod;

public interface ICommand {
    String getName();
    String getEndpoint();
    RequestMethod getMethod();

    String getDefaultResponseSuccess();
    String getDefaultResponseFailure();
}
