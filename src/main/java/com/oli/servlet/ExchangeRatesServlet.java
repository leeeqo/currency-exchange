package com.oli.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oli.dto.ExchangeRateRequest;
import com.oli.entity.ExchangeRate;
import com.oli.service.ExchangeRateService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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

        List<ExchangeRate> exchangeRates = null;
        try {
            exchangeRates = exchangeRateService.getAllExchangeRates();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ExchangeRateRequest exchangeRateRequest = new ObjectMapper().readValue(request.getReader(), ExchangeRateRequest.class);

        ExchangeRate saved = null;
        try {
            saved = exchangeRateService.saveExchangeRate(exchangeRateRequest);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), saved);
    }
}
