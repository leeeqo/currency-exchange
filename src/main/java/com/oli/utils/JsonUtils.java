package com.oli.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T readJsonFromRequest(HttpServletRequest request, Class<T> valueType) {
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(request.getReader(), valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static void writeJsonToResponse(HttpServletResponse response, Object obj) {
        try {
            OBJECT_MAPPER.writeValue(response.getWriter(), obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
