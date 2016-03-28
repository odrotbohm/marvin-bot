package io.pivotal.singapore.repositories.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArgumentListConverter implements AttributeConverter<List, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public List convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }
}
