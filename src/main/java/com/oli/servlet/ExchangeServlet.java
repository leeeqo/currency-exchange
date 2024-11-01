package com.oli.servlet;

import com.fasterxml.jackson.core.io.BigDecimalParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oli.dto.ExchangeRateResponse;
import com.oli.entity.Currency;
import com.oli.entity.ExchangeRate;
import com.oli.repository.CurrencyRepository;
import com.oli.repository.ExchangeRateRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.oli.repository.ExchangeRateRepository.INVALID_CURRENCY_CODE;

@WebServlet(name = "ExchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {

    private static final String USD = "USD";

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
            throws ServletException, IOException {

        String from = request.getParameter("from");
        String to = request.getParameter("to");
        BigDecimal amount = BigDecimalParser.parse(request.getParameter("amount"));

        BigDecimal rate = calculateRate(from, to);
        BigDecimal convertedAmount = amount.multiply(rate);

        Currency baseCurrency = currencyRepository.findByCode(from)
                .orElseThrow(() -> new NoSuchElementException(INVALID_CURRENCY_CODE));
        Currency targetCurrency = currencyRepository.findByCode(to)
                .orElseThrow(() -> new NoSuchElementException(INVALID_CURRENCY_CODE));

        ExchangeRateResponse exchangeRateResponse = ExchangeRateResponse.builder()
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(rate).amount(amount)
                .convertedAmount(convertedAmount)
                .build();

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), exchangeRateResponse);
    }

    private BigDecimal calculateRate(String from, String to) throws NoSuchElementException {
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCodes(from, to);

        if (exchangeRate.isPresent()) {
            return exchangeRate.get().getRate();
        }

        Optional<ExchangeRate> exchangeRateInverted = exchangeRateRepository.findByCodes(to, from);

        if (exchangeRateInverted.isPresent()) {
            return new BigDecimal(1).divide(exchangeRateInverted.get().getRate(), 2, RoundingMode.HALF_UP);
        }

        Optional<ExchangeRate> exchangeRateUSDToFirst = exchangeRateRepository.findByCodes(USD, from);
        Optional<ExchangeRate> exchangeRateUSDToSecond = exchangeRateRepository.findByCodes(USD, to);

        if (exchangeRateUSDToFirst.isPresent() && exchangeRateUSDToSecond.isPresent()) {
            BigDecimal rateUSDtoFirst = exchangeRateUSDToFirst.get().getRate();
            BigDecimal rateUSDtoSecond = exchangeRateUSDToSecond.get().getRate();

            return rateUSDtoSecond.divide(rateUSDtoFirst, 2, RoundingMode.HALF_UP);
        }

        throw new NoSuchElementException("There is no enough informaton about rates to calculate convertedAmount");
    }
}