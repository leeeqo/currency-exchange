package com.oli.servlet;

import com.oli.dto.ExchangeRateWithoutCodes;
import com.oli.entity.ExchangeRate;
import com.oli.exception.ApplicationException;
import com.oli.service.ExchangeRateService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.oli.exception.ExceptionHandler.handleException;
import static com.oli.utils.JsonUtils.readJsonFromRequest;
import static com.oli.utils.JsonUtils.writeJsonToResponse;
import static com.oli.utils.RequestUtils.getRequestCurrencyCodesList;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        exchangeRateService = (ExchangeRateService) servletConfig.getServletContext()
                .getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ExchangeRate exchangeRate = null;
        try {
            List<String> codes = getRequestCurrencyCodesList(request);

            exchangeRate = exchangeRateService.getExchangeRateByCodes(codes);
        } catch (ApplicationException e) {
            handleException(response, e);
        }

        writeJsonToResponse(response, exchangeRate);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ExchangeRate updated = null;
        try {
            List<String> codes = getRequestCurrencyCodesList(request);

            ExchangeRateWithoutCodes exchangeRateWithoutCodes = readJsonFromRequest(request, ExchangeRateWithoutCodes.class);

            updated = exchangeRateService.updateExchangeRate(exchangeRateWithoutCodes, codes);
        } catch (ApplicationException e) {
            handleException(response, e);
        }

        writeJsonToResponse(response, updated);
    }
}
