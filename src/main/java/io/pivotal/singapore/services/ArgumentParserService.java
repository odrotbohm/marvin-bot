package io.pivotal.singapore.services;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;


@Service
class ArgumentParserService {

    TreeMap parse(String s, TreeMap<String, String> argumentsConfig) {
        TreeMap<String, String> returnMap = new TreeMap<>();

        for (Map.Entry<String, String> e : argumentsConfig.entrySet()) {
            String regex = e.getValue();

            if (regex.startsWith("/")) {
                try {
                    String match = parseRegex(s, regex);
                    s = s.replace(match, "");
                    returnMap.put(e.getKey(), match);
                } catch (IndexOutOfBoundsException|IllegalStateException ex) {
                    throw new IllegalArgumentException(
                        String.format("Argument '%s' found no match with regex '%s'", e.getKey(), regex),
                        ex
                    );
                }
            } else if (regex.equals("TIMESTAMP")) {
                returnMap.put(e.getKey(), parseTimestamp(s));
            } else {
                throw new IllegalArgumentException(
                    String.format("The argument '%s' for '%s' is not valid", regex, e.getKey())
                );
            }
        }

        return returnMap;
    }

    private String parseRegex(String s, String regex) {
        regex = String.format("^%s", (String) regex.subSequence(1, regex.length() - 1));
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        m.find();
        MatchResult results = m.toMatchResult();

        return results.group(1);
    }

    private String parseTimestamp(String s) {
        List<DateGroup> parse = new PrettyTimeParser().parseSyntax(s);
        if (parse != null && !parse.isEmpty()) {
            Date d = parse.get(0).getDates().get(0);

            // TODO: Maybe not hardcode in Singapore here?
            LocalDateTime localDate = LocalDateTime.ofEpochSecond(d.getTime() / 1000, 0, ZoneOffset.of("+08:00"));
            return ZonedDateTime.of(localDate, ZoneOffset.of("+08:00")).format(ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

}
