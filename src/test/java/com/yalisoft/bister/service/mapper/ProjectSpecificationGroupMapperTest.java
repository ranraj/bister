package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectSpecificationGroupMapperTest {

    private ProjectSpecificationGroupMapper projectSpecificationGroupMapper;

    @BeforeEach
    public void setUp() {
        projectSpecificationGroupMapper = new ProjectSpecificationGroupMapperImpl();
    }
}
