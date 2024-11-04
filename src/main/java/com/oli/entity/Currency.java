package com.oli.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class Currency {

    private Long id;
    private String code;
    private String sign;

    @JsonProperty(value = "name")
    private String fullName;

    @JsonCreator
    public Currency(@JsonProperty(value = "code", required = true) String code,
                    @JsonProperty(value = "name", required = true) String fullName,
                    @JsonProperty(value = "sign", required = true) String sign) {

        this.id = null;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
