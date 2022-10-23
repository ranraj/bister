package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectSpecificationGroupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectSpecificationGroupDTO.class);
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO1 = new ProjectSpecificationGroupDTO();
        projectSpecificationGroupDTO1.setId(1L);
        ProjectSpecificationGroupDTO projectSpecificationGroupDTO2 = new ProjectSpecificationGroupDTO();
        assertThat(projectSpecificationGroupDTO1).isNotEqualTo(projectSpecificationGroupDTO2);
        projectSpecificationGroupDTO2.setId(projectSpecificationGroupDTO1.getId());
        assertThat(projectSpecificationGroupDTO1).isEqualTo(projectSpecificationGroupDTO2);
        projectSpecificationGroupDTO2.setId(2L);
        assertThat(projectSpecificationGroupDTO1).isNotEqualTo(projectSpecificationGroupDTO2);
        projectSpecificationGroupDTO1.setId(null);
        assertThat(projectSpecificationGroupDTO1).isNotEqualTo(projectSpecificationGroupDTO2);
    }
}
