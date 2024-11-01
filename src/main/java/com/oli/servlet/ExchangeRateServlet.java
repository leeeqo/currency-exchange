package com.oli.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oli.dto.ExchangeRateRequest;
import com.oli.entity.ExchangeRate;
import com.oli.repository.ExchangeRateRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateRepository exchangeRateRepository;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        exchangeRateRepository = (ExchangeRateRepository) servletConfig.getServletContext()
                .getAttribute("exchangeRateRepository");
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

        String codes = pathInfo.replaceFirst("/", "").toUpperCase();
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCodes(codes.substring(0, 3), codes.substring(3, 6));

        if (exchangeRate.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "ExchangeRate with codes " + codes + " wasn't found.");
            return;
        }

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), exchangeRate.get());
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        ExchangeRateRequest exchangeRateRequest = new ObjectMapper().readValue(request.getReader(), ExchangeRateRequest.class);

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Enter exchange rate codes. Ex.: .../exchangeRate/USDEUR");
            return;
        }

        String codes = pathInfo.replaceFirst("/", "").toUpperCase();
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCodes(codes.substring(0, 3), codes.substring(3, 6));

        if (exchangeRate.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "ExchangeRate with codes " + codes + " wasn't found.");
            return;
        }

        exchangeRate.get().setRate(exchangeRateRequest.getRate());
        ExchangeRate updated = exchangeRateRepository.update(exchangeRate.get());

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), updated);
    }
}
