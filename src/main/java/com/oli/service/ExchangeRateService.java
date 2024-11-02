package com.oli.service;

import com.oli.dto.ExchangeRateResponse;
import com.oli.entity.ExchangeRate;
import com.oli.repository.impl.ExchangeRateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeRateService {

    private static final String USD = "USD";

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public ExchangeRateResponse calculateExchangeRateResponse(String from, String to, BigDecimal amount)
            throws NoSuchElementException, SQLException {

        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCodes(from, to);

        if (exchangeRate.isPresent()) {
            BigDecimal rate = exchangeRate.get().getRate();

            BigDecimal convertedAmount = amount.multiply(rate);

            return ExchangeRateResponse.builder()
                    .baseCurrency(exchangeRate.get().getBaseCurrency())
                    .targetCurrency(exchangeRate.get().getTargetCurrency())
                    .rate(rate)
                    .amount(amount)
                    .convertedAmount(convertedAmount)
                    .build();
        }

        Optional<ExchangeRate> exchangeRateInverted = exchangeRateRepository.findByCodes(to, from);

        if (exchangeRateInverted.isPresent()) {
            BigDecimal rate = new BigDecimal(1).divide(exchangeRateInverted.get().getRate(), 2, RoundingMode.HALF_UP);

            BigDecimal convertedAmount = amount.multiply(rate);

            return ExchangeRateResponse.builder()
                    .baseCurrency(exchangeRateInverted.get().getTargetCurrency())
                    .targetCurrency(exchangeRateInverted.get().getBaseCurrency())
                    .rate(rate)
                    .amount(amount)
                    .convertedAmount(convertedAmount)
                    .build();
        }

        Optional<ExchangeRate> exchangeRateUSDToFirst = exchangeRateRepository.findByCodes(USD, from);
        Optional<ExchangeRate> exchangeRateUSDToSecond = exchangeRateRepository.findByCodes(USD, to);

        if (exchangeRateUSDToFirst.isPresent() && exchangeRateUSDToSecond.isPresent()) {
            BigDecimal rateUSDtoFirst = exchangeRateUSDToFirst.get().getRate();
            BigDecimal rateUSDtoSecond = exchangeRateUSDToSecond.get().getRate();

            BigDecimal rate = rateUSDtoSecond.divide(rateUSDtoFirst, 2, RoundingMode.HALF_UP);

            BigDecimal convertedAmount = amount.multiply(rate);

            return ExchangeRateResponse.builder()
                    .baseCurrency(exchangeRateUSDToFirst.get().getTargetCurrency())
                    .targetCurrency(exchangeRateUSDToSecond.get().getTargetCurrency())
                    .rate(rate)
                    .amount(amount)
                    .convertedAmount(convertedAmount)
                    .build();
        }

        throw new NoSuchElementException("Not enough information about exchange rates. " +
                "Converted amount can not be calculated.");
    }
}
