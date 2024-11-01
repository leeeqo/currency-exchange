package com.oli.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oli.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {

    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
