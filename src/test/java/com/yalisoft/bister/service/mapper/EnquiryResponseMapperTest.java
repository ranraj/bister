package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnquiryResponseMapperTest {

    private EnquiryResponseMapper enquiryResponseMapper;

    @BeforeEach
    public void setUp() {
        enquiryResponseMapper = new EnquiryResponseMapperImpl();
    }
}
