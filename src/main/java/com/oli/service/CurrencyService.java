package com.oli.service;

import com.oli.entity.Currency;
import com.oli.repository.impl.CurrencyRepository;
import com.oli.repository.impl.ExchangeRateRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class CurrencyService {

    private static final String NO_CURRENCY = "Currency with specified code was not found.";

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        return currencyRepository.findAll();
    }

    public Currency getCurrencyByCode(String code) throws SQLException {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException(NO_CURRENCY));
    }

    public Currency saveCurrency(Currency currency) throws SQLException {
        return currencyRepository.save(currency);
    }
}
