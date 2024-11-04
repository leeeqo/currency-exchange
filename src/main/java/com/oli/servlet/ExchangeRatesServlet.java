package com.oli.servlet;

import com.oli.dto.ExchangeRateWithCodes;
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

@WebServlet(name = "ExchangeRatesServlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        exchangeRateService = (ExchangeRateService) servletConfig.getServletContext()
                .getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ExchangeRate> exchangeRates = exchangeRateService.getAllExchangeRates();

        writeJsonToResponse(response, exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ExchangeRate saved = null;
        try {
            ExchangeRateWithCodes exchangeRateWithCodes = readJsonFromRequest(request, ExchangeRateWithCodes.class);

            saved = exchangeRateService.saveExchangeRate(exchangeRateWithCodes);
        } catch (ApplicationException e) {
            handleException(response, e);
        }

        writeJsonToResponse(response, saved);
    }
}
