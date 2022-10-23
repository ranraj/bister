package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectType.class);
        ProjectType projectType1 = new ProjectType();
        projectType1.setId(1L);
        ProjectType projectType2 = new ProjectType();
        projectType2.setId(projectType1.getId());
        assertThat(projectType1).isEqualTo(projectType2);
        projectType2.setId(2L);
        assertThat(projectType1).isNotEqualTo(projectType2);
        projectType1.setId(null);
        assertThat(projectType1).isNotEqualTo(projectType2);
    }
}
