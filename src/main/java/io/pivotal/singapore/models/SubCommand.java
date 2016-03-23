package io.pivotal.singapore.models;

import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "sub_commands")
public class SubCommand {
    @Id
    @SequenceGenerator(name = "pk_sequence", sequenceName = "commands_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private long id;

    private String name;

    private String endpoint;

    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="sub_command_arguments", joinColumns=@JoinColumn(name="sub_command_id"))
    private Map<String, String> arguments;

    private RequestMethod method;

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
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

    public Map<String, String> getArguments() {
        return arguments;
    }

    public void setArguments(LinkedHashMap<String, String> arguments) {
        this.arguments = arguments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
