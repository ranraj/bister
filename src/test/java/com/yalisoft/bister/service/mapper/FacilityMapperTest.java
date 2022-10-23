package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FacilityMapperTest {

    private FacilityMapper facilityMapper;

    @BeforeEach
    public void setUp() {
        facilityMapper = new FacilityMapperImpl();
    }
}
