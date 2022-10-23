package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectSpecificationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectSpecificationDTO.class);
        ProjectSpecificationDTO projectSpecificationDTO1 = new ProjectSpecificationDTO();
        projectSpecificationDTO1.setId(1L);
        ProjectSpecificationDTO projectSpecificationDTO2 = new ProjectSpecificationDTO();
        assertThat(projectSpecificationDTO1).isNotEqualTo(projectSpecificationDTO2);
        projectSpecificationDTO2.setId(projectSpecificationDTO1.getId());
        assertThat(projectSpecificationDTO1).isEqualTo(projectSpecificationDTO2);
        projectSpecificationDTO2.setId(2L);
        assertThat(projectSpecificationDTO1).isNotEqualTo(projectSpecificationDTO2);
        projectSpecificationDTO1.setId(null);
        assertThat(projectSpecificationDTO1).isNotEqualTo(projectSpecificationDTO2);
    }
}
