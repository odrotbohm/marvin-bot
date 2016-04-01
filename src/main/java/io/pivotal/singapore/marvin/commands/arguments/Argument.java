package io.pivotal.singapore.marvin.commands.arguments;

import io.pivotal.singapore.utils.Pair;

public interface Argument {
    static Boolean canParse(String capture) {
        return false;
    }

    Pair<Integer, String> parse(String rawCommand);

    String getName();
    String getPattern();

    String toJson();
}
