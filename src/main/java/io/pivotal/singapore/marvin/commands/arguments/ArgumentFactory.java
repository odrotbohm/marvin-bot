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

    static Argument getWithEmptyArgument(String name, String pattern) {
        try {
            return getArgument(name, pattern);
        } catch (IllegalArgumentException e) {
            return new InvalidArgument(name, pattern);
        }
    }
}
