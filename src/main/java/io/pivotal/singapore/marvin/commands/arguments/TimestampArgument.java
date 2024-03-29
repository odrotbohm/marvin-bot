package io.pivotal.singapore.marvin.commands.arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.singapore.utils.Pair;
import lombok.Getter;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class TimestampArgument implements Argument {
    private static final String MACRO_NAME = "TIMESTAMP";
    @Getter private String name;

    public TimestampArgument(String name) {
        this.name = name;
    }

    public static Boolean canParse(String capture) {
        return capture.equals(MACRO_NAME);
    }

    @Override
    public String getPattern() {
        return MACRO_NAME;
    }

    @Override
    public Pair<Integer, String> parse(String rawCommand) {
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

            zonedDateTime = zonedDateTime.withNano(0);

            return new Pair<>(charactersToRemove, zonedDateTime.format(ISO_OFFSET_DATE_TIME));
        } else {
            throw new IllegalArgumentException(
                String.format("Argument '%s' found no match for '%s' in text '%s'", name, MACRO_NAME, rawCommand)
            );
        }
    }

}
