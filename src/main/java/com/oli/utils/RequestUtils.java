package com.oli.utils;

import com.fasterxml.jackson.core.io.BigDecimalParser;
import com.oli.exception.impl.IncorrectParameterException;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RequestUtils {

    public static String getRequestCurrencyCode(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.charAt(0) != '/' || pathInfo.length() != 4) {
            throw new IncorrectParameterException(
                    "Specify currency code. Example: /currency/USD");
        }

        String code = pathInfo.replaceFirst("/", "").toUpperCase();

        validateCurrencyCode(code);

        return code;
    }

    public static List<String> getRequestCurrencyCodesList(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.charAt(0) != '/' || pathInfo.length() != 7) {
            throw new IncorrectParameterException(
                    "Specify currency codes. Example: /currency/USDEUR");
        }

        String baseCode = pathInfo.substring(1, 4).toUpperCase();
        String targetCode = pathInfo.substring(4, 7).toUpperCase();

        validateCurrencyCode(baseCode);
        validateCurrencyCode(targetCode);

        return List.of(baseCode, targetCode);
    }

    public static List<Object> getRequestExchangeParameters(HttpServletRequest request) {
        String from = request.getParameter("from").toUpperCase();
        String to = request.getParameter("to").toUpperCase();

        validateCurrencyCode(from);
        validateCurrencyCode(to);

        BigDecimal amount = BigDecimalParser.parse(request.getParameter("amount"))
                .setScale(2, RoundingMode.HALF_UP);

        return List.of(from, to, amount);
    }

    public static void validateCurrencyCode(String code) {
        if (code.length() != 3) {
            throw new IncorrectParameterException(
                    "Currency code has incorrect format. Example: /currency/USD");
        }
    }
}
