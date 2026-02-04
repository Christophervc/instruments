package com.aplication.rest.instruments.product.dto;

import com.aplication.rest.instruments.product.enums.InstrumentType;

import java.math.BigDecimal;

public record ProductSearchCriteria(String name,
                                    BigDecimal minPrice,
                                    BigDecimal maxPrice,
                                    String manufacturer,
                                    InstrumentType type) {
}

