package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaxRateMapperTest {

    private TaxRateMapper taxRateMapper;

    @BeforeEach
    public void setUp() {
        taxRateMapper = new TaxRateMapperImpl();
    }
}
