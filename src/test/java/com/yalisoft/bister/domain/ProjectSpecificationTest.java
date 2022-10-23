package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectSpecificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectSpecification.class);
        ProjectSpecification projectSpecification1 = new ProjectSpecification();
        projectSpecification1.setId(1L);
        ProjectSpecification projectSpecification2 = new ProjectSpecification();
        projectSpecification2.setId(projectSpecification1.getId());
        assertThat(projectSpecification1).isEqualTo(projectSpecification2);
        projectSpecification2.setId(2L);
        assertThat(projectSpecification1).isNotEqualTo(projectSpecification2);
        projectSpecification1.setId(null);
        assertThat(projectSpecification1).isNotEqualTo(projectSpecification2);
    }
}
