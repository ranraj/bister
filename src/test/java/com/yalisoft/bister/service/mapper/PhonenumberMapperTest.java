package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PhonenumberMapperTest {

    private PhonenumberMapper phonenumberMapper;

    @BeforeEach
    public void setUp() {
        phonenumberMapper = new PhonenumberMapperImpl();
    }
}
