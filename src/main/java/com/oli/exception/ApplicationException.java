package com.oli.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final int httpResponseCode;
    private final String additionalMessage;

    public ApplicationException(String additionalMessage, int httpResponseCode) {
        this.additionalMessage = additionalMessage;
        this.httpResponseCode = httpResponseCode;
    }
}
