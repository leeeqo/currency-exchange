package com.oli.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ExchangeRateWithoutCodes {

    private BigDecimal rate;

    @JsonCreator
    public ExchangeRateWithoutCodes(@JsonProperty(value = "rate", required = true) BigDecimal rate) {
        this.rate = rate;
    }
}
