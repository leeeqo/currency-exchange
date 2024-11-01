package com.oli.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oli.entity.Currency;
import com.oli.repository.impl.CurrencyRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), currencyRepository.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Currency currency = new ObjectMapper().readValue(request.getReader(), Currency.class);

        if (currencyRepository.findByCode(currency.getCode()).isPresent()) {
            response.sendError(HttpServletResponse.SC_CONFLICT,
                    "Currency with code " + currency.getCode() + " already exists.");
            return;
        }

        Currency saved = currencyRepository.save(currency);

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), saved);
    }
}
