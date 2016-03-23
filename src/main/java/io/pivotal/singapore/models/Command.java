package io.pivotal.singapore.models;

import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "commands")
public class Command {

    @Id
    @SequenceGenerator(name = "pk_sequence", sequenceName = "commands_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private long id;
    @Column(unique = true)
    private String name;
    private String endpoint;

    private RequestMethod method;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name="COMMAND_ID", referencedColumnName="ID")
    public List<SubCommand> subCommands;

    public Command(String name, String endpoint) {
        this.name = name;
        this.endpoint = endpoint;
        this.method = RequestMethod.POST;
    }

    public Command() {} // to make JPA happy....

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

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    public void setSubCommands(List<SubCommand> subCommands) {
        this.subCommands = subCommands;
    }
}
