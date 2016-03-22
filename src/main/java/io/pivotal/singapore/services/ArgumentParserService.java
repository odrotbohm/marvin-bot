package io.pivotal.singapore.services;

import io.pivotal.singapore.utils.Pair;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;


@Service
class ArgumentParserService {

    Map parse(String rawCommand, LinkedHashMap<String, String> argumentsConfig) {
        TreeMap<String, String> returnMap = new TreeMap<>();

        for (Map.Entry<String, String> captureGroup : argumentsConfig.entrySet()) {
            String regex = captureGroup.getValue();
            rawCommand = rawCommand.trim();

            if (regex.startsWith("/")) {
                Pair<Integer, String> match = parseRegex(rawCommand, captureGroup);
                rawCommand = rawCommand.subSequence(match.first, rawCommand.length()).toString();
                returnMap.put(captureGroup.getKey(), match.last);
            } else if (regex.equals("TIMESTAMP")) {
                Pair<Integer, String> match = parseTimestamp(rawCommand, captureGroup);
                rawCommand = rawCommand.subSequence(match.first, rawCommand.length()).toString();
                returnMap.put(captureGroup.getKey(), match.last);
            } else {
                throw new IllegalArgumentException(
                    String.format("The argument '%s' for '%s' is not valid", regex, captureGroup.getKey())
                );
            }
        }

        return returnMap;
    }

    private Pair<Integer, String> parseRegex(String rawCommand, Map.Entry captureGroup) throws IllegalArgumentException {
        String regex = (String) captureGroup.getValue();
        regex = String.format("^%s", (String) regex.subSequence(1, regex.length() - 1));
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(rawCommand);

        try {
            m.find();
            MatchResult results = m.toMatchResult();

            return new Pair<>(m.end(0), results.group(1));
        } catch (IndexOutOfBoundsException | IllegalStateException ex) {
            throw new IllegalArgumentException(
                String.format("Argument '%s' found no match with regex '%s' in '%s'", captureGroup.getKey(), regex, rawCommand),
                ex
            );
        }
    }

    // TODO: Maybe not hardcode in Singapore here?
    private Pair<Integer, String> parseTimestamp(String rawCommand, Map.Entry captureGroup) throws IllegalArgumentException {
        List<DateGroup> parse = new PrettyTimeParser().parseSyntax(rawCommand);
        if (parse != null && !parse.isEmpty()) {
            DateGroup dateGroup = parse.get(0);
            Date d = dateGroup.getDates().get(0);
            Integer charactersToRemove = dateGroup.getPosition() + dateGroup.getText().length();
            ZoneId defaultTimezone = ZoneId.of("+08:00");

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
                zonedDateTime = zonedDateTime.withZoneSameLocal(defaultTimezone);
            } else { // Correct time, wrong display time
                zonedDateTime = zonedDateTime.withZoneSameInstant(defaultTimezone);
            }

            return new Pair<>(charactersToRemove, zonedDateTime.format(ISO_OFFSET_DATE_TIME));
        } else {
            throw new IllegalArgumentException(
                String.format("Argument '%s' found no match for '%s' in text '%s'", captureGroup.getKey(), captureGroup.getValue(), rawCommand)
            );
        }
    }

}
