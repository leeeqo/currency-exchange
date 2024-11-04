package com.oli.exception.impl;

import com.oli.exception.ApplicationException;

public class NotFoundException extends ApplicationException {

    private static final int HTTP_RESPONSE_CODE = 404;

    public NotFoundException(String additionalMessage) {
        super(additionalMessage, HTTP_RESPONSE_CODE);
    }
}
