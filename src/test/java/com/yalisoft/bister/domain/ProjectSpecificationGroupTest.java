package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectSpecificationGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectSpecificationGroup.class);
        ProjectSpecificationGroup projectSpecificationGroup1 = new ProjectSpecificationGroup();
        projectSpecificationGroup1.setId(1L);
        ProjectSpecificationGroup projectSpecificationGroup2 = new ProjectSpecificationGroup();
        projectSpecificationGroup2.setId(projectSpecificationGroup1.getId());
        assertThat(projectSpecificationGroup1).isEqualTo(projectSpecificationGroup2);
        projectSpecificationGroup2.setId(2L);
        assertThat(projectSpecificationGroup1).isNotEqualTo(projectSpecificationGroup2);
        projectSpecificationGroup1.setId(null);
        assertThat(projectSpecificationGroup1).isNotEqualTo(projectSpecificationGroup2);
    }
}
