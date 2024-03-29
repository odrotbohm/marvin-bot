package io.pivotal.singapore.marvin.commands.arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.pivotal.singapore.utils.Pair;

public interface Argument {
    static Boolean canParse(String capture) {
        return false;
    }

    Pair<Integer, String> parse(String rawCommand);

    String getName();
    String getPattern();
}
