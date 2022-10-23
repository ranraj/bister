package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectActivityMapperTest {

    private ProjectActivityMapper projectActivityMapper;

    @BeforeEach
    public void setUp() {
        projectActivityMapper = new ProjectActivityMapperImpl();
    }
}
