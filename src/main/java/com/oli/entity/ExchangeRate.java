package com.oli.entity;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
}
