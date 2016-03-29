package io.pivotal.singapore.models;

import io.pivotal.singapore.repositories.converters.ArgumentListConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Table(name = "sub_commands")
public class SubCommand implements ICommand {
    @Id
    @SequenceGenerator(name = "pk_sequence", sequenceName = "commands_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private long id;
    private String name;
    private String endpoint;
    @Getter @Setter private String defaultResponseSuccess;
    @Getter @Setter private String defaultResponseFail;

    private RequestMethod method;

    @Convert(converter = ArgumentListConverter.class)
    private List<Map<String,String>> arguments;
}
