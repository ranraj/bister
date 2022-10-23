package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductActivityMapperTest {

    private ProductActivityMapper productActivityMapper;

    @BeforeEach
    public void setUp() {
        productActivityMapper = new ProductActivityMapperImpl();
    }
}
