package com.oli.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateRequest {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;

    public ExchangeRateRequest(
            String baseCurrencyCode,
            String targetCurrencyCode,
            BigDecimal rate) {

        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.rate = rate;
    }

    public ExchangeRateRequest(BigDecimal rate) {
        this(null, null, rate);
    }
}
