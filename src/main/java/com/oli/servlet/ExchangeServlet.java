package com.oli.servlet;

import com.fasterxml.jackson.core.io.BigDecimalParser;
import com.oli.dto.ExchangeRateResponse;
import com.oli.service.ExchangeRateService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import static com.oli.utils.JsonUtils.writeJsonToResponse;

@WebServlet(name = "ExchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        exchangeRateService = (ExchangeRateService) servletConfig.getServletContext()
                .getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String from = request.getParameter("from");
        String to = request.getParameter("to");
        BigDecimal amount = BigDecimalParser.parse(request.getParameter("amount"));

        ExchangeRateResponse exchangeRateResponse = null;
        try {
            exchangeRateResponse = exchangeRateService.calculateExchangeRateResponse(from, to, amount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        writeJsonToResponse(response, exchangeRateResponse);
    }
}
