package com.oli.servlet;

import com.oli.entity.Currency;
import com.oli.repository.impl.CurrencyRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.oli.utils.JsonUtils.readJsonFromRequest;
import static com.oli.utils.JsonUtils.writeJsonToResponse;

@WebServlet(name = "CurrenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyRepository currencyRepository;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        currencyRepository = (CurrencyRepository) servletConfig.getServletContext()
                .getAttribute("currencyRepository");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Currency> currencies = currencyRepository.findAll();

        writeJsonToResponse(response, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Currency currency = readJsonFromRequest(request, Currency.class);

        Currency saved = currencyRepository.save(currency);

        writeJsonToResponse(response, saved);
    }
}
