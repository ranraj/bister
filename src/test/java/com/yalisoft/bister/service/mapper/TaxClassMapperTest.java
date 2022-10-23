package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaxClassMapperTest {

    private TaxClassMapper taxClassMapper;

    @BeforeEach
    public void setUp() {
        taxClassMapper = new TaxClassMapperImpl();
    }
}
