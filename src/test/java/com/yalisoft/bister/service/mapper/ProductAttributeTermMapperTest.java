package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductAttributeTermMapperTest {

    private ProductAttributeTermMapper productAttributeTermMapper;

    @BeforeEach
    public void setUp() {
        productAttributeTermMapper = new ProductAttributeTermMapperImpl();
    }
}
