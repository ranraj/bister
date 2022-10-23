package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductAttributeMapperTest {

    private ProductAttributeMapper productAttributeMapper;

    @BeforeEach
    public void setUp() {
        productAttributeMapper = new ProductAttributeMapperImpl();
    }
}
