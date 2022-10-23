package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnquiryMapperTest {

    private EnquiryMapper enquiryMapper;

    @BeforeEach
    public void setUp() {
        enquiryMapper = new EnquiryMapperImpl();
    }
}
