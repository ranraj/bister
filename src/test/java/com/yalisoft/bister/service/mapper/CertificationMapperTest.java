package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CertificationMapperTest {

    private CertificationMapper certificationMapper;

    @BeforeEach
    public void setUp() {
        certificationMapper = new CertificationMapperImpl();
    }
}
