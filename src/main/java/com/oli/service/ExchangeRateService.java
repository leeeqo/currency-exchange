package com.oli.service;

import com.oli.dto.ExchangeRateRequest;
import com.oli.dto.ExchangeRateResponse;
import com.oli.entity.Currency;
import com.oli.entity.ExchangeRate;
import com.oli.repository.impl.CurrencyRepository;
import com.oli.repository.impl.ExchangeRateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeRateService {

    private static final String USD = "USD";
    private static final String INVALID_CURRENCY_CODE = "Invalid currency code." +
            "There is no currency with code that is specified for current exchange rate.";
    private static final String NO_EXCHANGE_RATE = "Exchange rate with specified codes was not found.";

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyRepository currencyRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, CurrencyRepository currencyRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyRepository = currencyRepository;
    }

    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        return exchangeRateRepository.findAll();
    }

    public ExchangeRate getExchangeRateByCodes(String codes) throws NoSuchElementException, SQLException {
        String baseCode = codes.substring(0, 3).toUpperCase();
        String targetCode = codes.substring(3, 6).toUpperCase();

        return exchangeRateRepository.findByCodes(baseCode, targetCode)
                .orElseThrow(() -> new NoSuchElementException(NO_EXCHANGE_RATE));
    }

    public ExchangeRate updateExchangeRate(ExchangeRateRequest request, String codes)
            throws NoSuchElementException, SQLException {

        String baseCode = codes.substring(0, 3).toUpperCase();
        String targetCode = codes.substring(3, 6).toUpperCase();

        ExchangeRate exchangeRate = exchangeRateRepository.findByCodes(baseCode, targetCode)
                .orElseThrow(() -> new NoSuchElementException(NO_EXCHANGE_RATE));

        exchangeRate.setRate(request.getRate());

        return exchangeRateRepository.update(exchangeRate);
    }

    public ExchangeRate saveExchangeRate(ExchangeRateRequest exchangeRateRequest)
            throws NoSuchElementException, SQLException {

        Currency baseCurrency = currencyRepository.findByCode(exchangeRateRequest.getBaseCurrencyCode())
                .orElseThrow(() -> new NoSuchElementException(INVALID_CURRENCY_CODE));
        Currency targetCurrency = currencyRepository.findByCode(exchangeRateRequest.getTargetCurrencyCode())
                .orElseThrow(() -> new NoSuchElementException(INVALID_CURRENCY_CODE));

        ExchangeRate exchangeRate = ExchangeRate.builder()
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(exchangeRateRequest.getRate())
                .build();

        return exchangeRateRepository.save(exchangeRate);
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
