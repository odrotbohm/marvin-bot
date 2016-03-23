package io.pivotal.singapore.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.*;

@Entity
@Table(name = "commands")
public class Command {

    @Id
    @SequenceGenerator(name = "pk_sequence", sequenceName = "commands_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    @Getter @Setter private long id;

    @Column(unique = true)
    @Getter @Setter private String name;
    @Getter @Setter private String endpoint;

    @Getter @Setter private RequestMethod method;

    public Command(String name, String endpoint) {
        this.name = name;
        this.endpoint = endpoint;
        this.method = RequestMethod.POST;
    }

    public Command() {} // to make JPA happy....
}
