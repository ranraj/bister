package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductReviewMapperTest {

    private ProductReviewMapper productReviewMapper;

    @BeforeEach
    public void setUp() {
        productReviewMapper = new ProductReviewMapperImpl();
    }
}
