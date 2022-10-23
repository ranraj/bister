package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectReviewMapperTest {

    private ProjectReviewMapper projectReviewMapper;

    @BeforeEach
    public void setUp() {
        projectReviewMapper = new ProjectReviewMapperImpl();
    }
}
