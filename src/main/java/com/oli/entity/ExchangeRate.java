package com.oli.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    private Long id;
    private Currency baseCurrencyId;
    private Currency targetCurrencyId;
    private BigDecimal rate;
}
