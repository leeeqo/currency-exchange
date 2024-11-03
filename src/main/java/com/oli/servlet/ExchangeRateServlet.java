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

import static com.oli.utils.JsonUtils.readJsonFromRequest;
import static com.oli.utils.JsonUtils.writeJsonToResponse;

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

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Enter exchange rate codes. Ex.: .../exchangeRate/USDEUR");
            return;
        }
        String codes = pathInfo.replaceFirst("/", "");

        ExchangeRate exchangeRate = null;
        try {
            exchangeRate = exchangeRateService.getExchangeRateByCodes(codes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        writeJsonToResponse(response, exchangeRate);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Enter exchange rate codes. Ex.: .../exchangeRate/USDEUR");
            return;
        }
        String codes = pathInfo.replaceFirst("/", "");

        ExchangeRateRequest exchangeRateRequest = readJsonFromRequest(request, ExchangeRateRequest.class);

        ExchangeRate updated = null;
        try {
            updated = exchangeRateService.updateExchangeRate(exchangeRateRequest, codes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        writeJsonToResponse(response, updated);
    }
}
