package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectTypeMapperTest {

    private ProjectTypeMapper projectTypeMapper;

    @BeforeEach
    public void setUp() {
        projectTypeMapper = new ProjectTypeMapperImpl();
    }
}
