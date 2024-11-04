package com.oli.utils;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oli.exception.impl.IncorrectParameterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T readJsonFromRequest(HttpServletRequest request, Class<T> valueType) throws IOException {
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(request.getReader(), valueType);
        } catch (DatabindException e) {
            throw new IncorrectParameterException("Incorrect input data.");
        }

        return obj;
    }

    public static void writeJsonToResponse(HttpServletResponse response, Object obj) throws IOException {
        OBJECT_MAPPER.writeValue(response.getWriter(), obj);
    }
}
