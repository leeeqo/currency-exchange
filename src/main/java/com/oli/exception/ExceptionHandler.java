package com.oli.exception;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ExceptionHandler {

    public static void handleException(HttpServletResponse response, ApplicationException e) throws IOException {
        response.sendError(e.getHttpResponseCode(), e.getAdditionalMessage());
    }
}
