package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectTypeDTO.class);
        ProjectTypeDTO projectTypeDTO1 = new ProjectTypeDTO();
        projectTypeDTO1.setId(1L);
        ProjectTypeDTO projectTypeDTO2 = new ProjectTypeDTO();
        assertThat(projectTypeDTO1).isNotEqualTo(projectTypeDTO2);
        projectTypeDTO2.setId(projectTypeDTO1.getId());
        assertThat(projectTypeDTO1).isEqualTo(projectTypeDTO2);
        projectTypeDTO2.setId(2L);
        assertThat(projectTypeDTO1).isNotEqualTo(projectTypeDTO2);
        projectTypeDTO1.setId(null);
        assertThat(projectTypeDTO1).isNotEqualTo(projectTypeDTO2);
    }
}
