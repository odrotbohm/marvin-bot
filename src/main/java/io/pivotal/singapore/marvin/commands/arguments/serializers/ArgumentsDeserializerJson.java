package io.pivotal.singapore.marvin.commands.arguments.serializers;

import com.fasterxml.jackson.databind.util.StdConverter;
import io.pivotal.singapore.marvin.commands.arguments.Arguments;

import java.util.List;
import java.util.Map;

public class ArgumentsDeserializerJson extends StdConverter<List<Map<String, String>>, Arguments> {
    @Override
    public Arguments convert(List<Map<String, String>> value) {
        return new Arguments(value);
    }
}
