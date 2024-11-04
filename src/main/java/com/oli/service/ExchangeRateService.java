package com.oli.service;

import com.oli.dto.ConvertedAmount;
import com.oli.dto.ExchangeRateWithCodes;
import com.oli.dto.ExchangeRateWithoutCodes;
import com.oli.entity.Currency;
import com.oli.entity.ExchangeRate;
import com.oli.exception.impl.NotFoundException;
import com.oli.repository.impl.ExchangeRateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {

    private static final String USD = "USD";

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    public ExchangeRate getExchangeRateByCodes(List<String> codes) {
        String baseCode = codes.get(0);
        String targetCode = codes.get(1);

        return exchangeRateRepository.findByCodes(baseCode, targetCode)
                .orElseThrow(() -> new NotFoundException(
                        "Exchange rate from " + baseCode + " to " + targetCode + " was not found."));
    }

    public ExchangeRate updateExchangeRate(ExchangeRateWithoutCodes exchangeRateWithoutCodes, List<String> codes) {
        String baseCode = codes.get(0);
        String targetCode = codes.get(1);

        ExchangeRate exchangeRate = ExchangeRate.builder()
                .baseCurrency(Currency.builder()
                        .code(baseCode)
                        .build())
                .targetCurrency(Currency.builder()
                        .code(targetCode)
                        .build())
                .rate(exchangeRateWithoutCodes.getRate())
                .build();

        return exchangeRateRepository.update(exchangeRate);
    }

    public ExchangeRate saveExchangeRate(ExchangeRateWithCodes exchangeRateWithCodes) {
        ExchangeRate exchangeRate = ExchangeRate.builder()
                .baseCurrency(Currency.builder()
                        .code(exchangeRateWithCodes.getBaseCurrencyCode())
                        .build())
                .targetCurrency(Currency.builder()
                        .code(exchangeRateWithCodes.getTargetCurrencyCode())
                        .build())
                .rate(exchangeRateWithCodes.getRate())
                .build();

        return exchangeRateRepository.save(exchangeRate);
    }

    public ConvertedAmount calculateExchangeRateResponse(List<Object> parameters) {
        String from = (String) parameters.get(0);
        String to = (String) parameters.get(1);
        BigDecimal amount = (BigDecimal) parameters.get(2);

        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCodes(from, to);

        if (exchangeRate.isPresent()) {
            BigDecimal rate = exchangeRate.get().getRate();

            BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

            return ConvertedAmount.builder()
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

            BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

            return ConvertedAmount.builder()
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

            BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

            return ConvertedAmount.builder()
                    .baseCurrency(exchangeRateUSDToFirst.get().getTargetCurrency())
                    .targetCurrency(exchangeRateUSDToSecond.get().getTargetCurrency())
                    .rate(rate)
                    .amount(amount)
                    .convertedAmount(convertedAmount)
                    .build();
        }

        throw new NotFoundException("Not enough information about exchange rates. Choose other currencies.");
    }
}
