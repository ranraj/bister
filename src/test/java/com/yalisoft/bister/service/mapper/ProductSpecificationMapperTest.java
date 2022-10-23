package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductSpecificationMapperTest {

    private ProductSpecificationMapper productSpecificationMapper;

    @BeforeEach
    public void setUp() {
        productSpecificationMapper = new ProductSpecificationMapperImpl();
    }
}
