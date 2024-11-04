package com.oli.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
public class ExchangeRateWithCodes {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;

    @JsonCreator
    public ExchangeRateWithCodes(@JsonProperty(value = "baseCurrencyCode", required = true) String baseCurrencyCode,
                                 @JsonProperty(value = "targetCurrencyCode", required = true) String targetCurrencyCode,
                                 @JsonProperty(value = "rate", required = true) BigDecimal rate) {

        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.rate = rate;
    }
}
