package io.pivotal.singapore.marvin.commands.arguments.serializers;

import com.fasterxml.jackson.databind.util.StdConverter;
import io.pivotal.singapore.marvin.commands.arguments.Arguments;

import java.util.List;
import java.util.Map;

public class ArgumentsSerializerJson extends StdConverter<Arguments, List<Map<String, String>>> {
    @Override
    public List<Map<String, String>> convert(Arguments arguments) {
        return arguments.toList();
    }
}
