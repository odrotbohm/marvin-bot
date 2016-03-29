package io.pivotal.singapore.utils;

import lombok.Getter;

import java.util.Map;

public class RemoteApiServiceResponse {
    private Boolean success;
    @Getter private Map<String, String> body;

    public RemoteApiServiceResponse(Boolean successful, Map<String, String> body) {
        this.success = successful;
        this.body = body;
    }

    public Boolean isSuccessful() {
        return success;
    }
}
