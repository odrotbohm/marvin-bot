package io.pivotal.singapore.services;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
class ArgumentParserService {

    Optional parseTimestamp(String s) {
        List<DateGroup> parse = new PrettyTimeParser().parseSyntax(s);
        if (parse != null && !parse.isEmpty()) {
            Date d = parse.get(0).getDates().get(0);

            // TODO: Maybe not hardcode in Singapore here?
            return Optional.of(LocalDateTime.ofEpochSecond(d.getTime() / 1000, 0, ZoneOffset.of("+08:00")));
        } else {
            return Optional.empty();
        }
    }
}
