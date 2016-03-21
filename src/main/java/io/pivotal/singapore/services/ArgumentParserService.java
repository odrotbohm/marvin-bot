package io.pivotal.singapore.services;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
            s = s.trim();

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

    // TODO: Maybe not hardcode in Singapore here?
    private String parseTimestamp(String s) {
        List<DateGroup> parse = new PrettyTimeParser().parseSyntax(s);
        if (parse != null && !parse.isEmpty()) {
            Date d = parse.get(0).getDates().get(0);

            /* $)(&*%)(@# Timezones.
             * The original timezone offset is only available from calendar instances.
             *
             * NOTE: The paths taken by this code is very different depending on your local
             * timezone. If you intend to change it at least try it out in two very different zones.
             * Current testing has been done using TZ=UTC and TZ=Asia/Singapore.
             *
             * Definitions:
             *   display time: The visible time, e.g. 19:00 +00:00. Ignoring the timezone the
             *                 time displayed is correct.
             *   correct time: The underlying time(stamp) is correct, but it's displayed with the
             *                 wrong timezone. E.g. 11:00 +00:00 which should be 19:00 +08:00.
             */
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(d);
            ZonedDateTime zonedDateTime = calendar
                .toInstant()
                .atZone(ZoneOffset.of("+00:00")); // Forces the timezone to UTC for *all*
            if (calendar.getTimeZone().getRawOffset() == 0) { // Correct display time, wrong time
                zonedDateTime = zonedDateTime.withZoneSameLocal(ZoneId.of("+08:00"));
            } else { // Correct time, wrong display time
                zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("+08:00"));
            }

            return zonedDateTime.format(ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

}
