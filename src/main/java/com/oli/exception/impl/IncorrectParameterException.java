package com.oli.exception.impl;

import com.oli.exception.ApplicationException;

public class IncorrectParameterException extends ApplicationException {

    private static final int HTTP_RESPONSE_CODE = 400;

    public IncorrectParameterException(String additionalMessage) {
        super(additionalMessage, HTTP_RESPONSE_CODE);
    }
}
