package com.oli.servlet;

import com.oli.entity.Currency;
import com.oli.service.CurrencyService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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

        List<Currency> currencies = null;
        try {
            currencies = currencyService.getAllCurrencies();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        writeJsonToResponse(response, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Currency currency = readJsonFromRequest(request, Currency.class);

        Currency saved = null;
        try {
            saved = currencyService.saveCurrency(currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        writeJsonToResponse(response, saved);
    }
}
