package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectActivityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectActivityDTO.class);
        ProjectActivityDTO projectActivityDTO1 = new ProjectActivityDTO();
        projectActivityDTO1.setId(1L);
        ProjectActivityDTO projectActivityDTO2 = new ProjectActivityDTO();
        assertThat(projectActivityDTO1).isNotEqualTo(projectActivityDTO2);
        projectActivityDTO2.setId(projectActivityDTO1.getId());
        assertThat(projectActivityDTO1).isEqualTo(projectActivityDTO2);
        projectActivityDTO2.setId(2L);
        assertThat(projectActivityDTO1).isNotEqualTo(projectActivityDTO2);
        projectActivityDTO1.setId(null);
        assertThat(projectActivityDTO1).isNotEqualTo(projectActivityDTO2);
    }
}
