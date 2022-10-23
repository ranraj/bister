package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductVariationAttributeTermMapperTest {

    private ProductVariationAttributeTermMapper productVariationAttributeTermMapper;

    @BeforeEach
    public void setUp() {
        productVariationAttributeTermMapper = new ProductVariationAttributeTermMapperImpl();
    }
}
