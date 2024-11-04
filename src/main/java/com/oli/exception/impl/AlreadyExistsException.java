package com.oli.exception.impl;

import com.oli.exception.ApplicationException;

public class AlreadyExistsException extends ApplicationException {

    private static final int HTTP_RESPONSE_CODE = 409;

    public AlreadyExistsException(String additionalMessage) {
        super(additionalMessage, HTTP_RESPONSE_CODE);
    }
}
