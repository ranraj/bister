package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductSpecificationGroupMapperTest {

    private ProductSpecificationGroupMapper productSpecificationGroupMapper;

    @BeforeEach
    public void setUp() {
        productSpecificationGroupMapper = new ProductSpecificationGroupMapperImpl();
    }
}
