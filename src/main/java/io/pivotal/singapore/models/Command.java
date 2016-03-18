package io.pivotal.singapore.models;

/**
 * Created by neo on 18/3/16.
 */
public class Command {
    private long id;
    private String name;
    private String endpoint;

    public Command(String name, String endpoint) {
        this.id = 0 ; // for now hard code all IDs to be 0....
        this.name = name;
        this.endpoint = endpoint;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }


}
