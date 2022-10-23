package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductVariationMapperTest {

    private ProductVariationMapper productVariationMapper;

    @BeforeEach
    public void setUp() {
        productVariationMapper = new ProductVariationMapperImpl();
    }
}
