package com.oli.servlet;

import com.oli.entity.Currency;
import com.oli.exception.ApplicationException;
import com.oli.service.CurrencyService;
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

@WebServlet(name = "CurrenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        currencyService = (CurrencyService) servletConfig.getServletContext()
                .getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Currency> currencies = currencyService.getAllCurrencies();

        writeJsonToResponse(response, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Currency saved = null;
        try {
            Currency currency = readJsonFromRequest(request, Currency.class);

            saved = currencyService.saveCurrency(currency);
        } catch (ApplicationException e) {
            handleException(response, e);
        }

        writeJsonToResponse(response, saved);
    }
}
