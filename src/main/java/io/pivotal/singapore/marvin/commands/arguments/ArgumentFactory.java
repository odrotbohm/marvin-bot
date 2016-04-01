package io.pivotal.singapore.marvin.commands.arguments;

class ArgumentFactory {
    static Argument getArgument(String name, String pattern) {

        if (RegexArgument.canParse(pattern)) {
            return new RegexArgument(name, pattern);
        } else if (TimestampArgument.canParse(pattern)) {
            return new TimestampArgument(name);
        } else {
            throw new IllegalArgumentException(String.format("Unknown factory for '%s'", pattern));
        }
    }
}
