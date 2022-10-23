package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectSpecificationMapperTest {

    private ProjectSpecificationMapper projectSpecificationMapper;

    @BeforeEach
    public void setUp() {
        projectSpecificationMapper = new ProjectSpecificationMapperImpl();
    }
}
