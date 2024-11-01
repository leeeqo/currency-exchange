package com.oli.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    private Long id;
    private String code;
    private String fullName;
    private String sign;
}
