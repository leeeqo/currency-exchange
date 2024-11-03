package com.oli.servlet;

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

        List<ExchangeRate> exchangeRates = null;
        try {
            exchangeRates = exchangeRateService.getAllExchangeRates();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        writeJsonToResponse(response, exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ExchangeRateRequest exchangeRateRequest = readJsonFromRequest(request, ExchangeRateRequest.class);

        ExchangeRate saved = null;
        try {
            saved = exchangeRateService.saveExchangeRate(exchangeRateRequest);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        writeJsonToResponse(response, saved);
    }
}
