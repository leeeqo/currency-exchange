package com.oli.service;

import com.oli.entity.Currency;
import com.oli.exception.impl.AlreadyExistsException;
import com.oli.exception.impl.NotFoundException;
import com.oli.repository.impl.CurrencyRepository;

import java.util.List;

public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public Currency getCurrencyByCode(String code) throws NotFoundException {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException(
                        "Currency with code " + code + " was not found."));
    }

    public Currency saveCurrency(Currency currency) throws AlreadyExistsException {
        return currencyRepository.save(currency);
    }
}
