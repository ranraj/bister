package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectActivityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectActivity.class);
        ProjectActivity projectActivity1 = new ProjectActivity();
        projectActivity1.setId(1L);
        ProjectActivity projectActivity2 = new ProjectActivity();
        projectActivity2.setId(projectActivity1.getId());
        assertThat(projectActivity1).isEqualTo(projectActivity2);
        projectActivity2.setId(2L);
        assertThat(projectActivity1).isNotEqualTo(projectActivity2);
        projectActivity1.setId(null);
        assertThat(projectActivity1).isNotEqualTo(projectActivity2);
    }
}
