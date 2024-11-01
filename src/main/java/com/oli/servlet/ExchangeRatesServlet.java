package com.oli.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oli.dto.ExchangeRateRequest;
import com.oli.entity.ExchangeRate;
import com.oli.repository.impl.CurrencyRepository;
import com.oli.repository.impl.ExchangeRateRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.oli.repository.impl.ExchangeRateRepository.INVALID_CURRENCY_CODE;

@WebServlet(name = "ExchangeRatesServlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private CurrencyRepository currencyRepository;
    private ExchangeRateRepository exchangeRateRepository;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        currencyRepository = (CurrencyRepository) servletConfig.getServletContext()
                .getAttribute("currencyRepository");
        exchangeRateRepository = (ExchangeRateRepository) servletConfig.getServletContext()
                .getAttribute("exchangeRateRepository");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), exchangeRateRepository.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        ExchangeRateRequest exchangeRateRequest = new ObjectMapper().readValue(request.getReader(), ExchangeRateRequest.class);

        ExchangeRate exchangeRate = ExchangeRate.builder()
                .baseCurrencyId(currencyRepository.findByCode(exchangeRateRequest.getBaseCurrencyCode())
                        .orElseThrow(() -> new NoSuchElementException(INVALID_CURRENCY_CODE)))
                .targetCurrencyId(currencyRepository.findByCode(exchangeRateRequest.getTargetCurrencyCode())
                        .orElseThrow(() -> new NoSuchElementException(INVALID_CURRENCY_CODE)))
                .rate(exchangeRateRequest.getRate())
                .build();

        ExchangeRate saved = exchangeRateRepository.save(exchangeRate);

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), saved);
    }
}
