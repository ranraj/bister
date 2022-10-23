package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectReviewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectReviewDTO.class);
        ProjectReviewDTO projectReviewDTO1 = new ProjectReviewDTO();
        projectReviewDTO1.setId(1L);
        ProjectReviewDTO projectReviewDTO2 = new ProjectReviewDTO();
        assertThat(projectReviewDTO1).isNotEqualTo(projectReviewDTO2);
        projectReviewDTO2.setId(projectReviewDTO1.getId());
        assertThat(projectReviewDTO1).isEqualTo(projectReviewDTO2);
        projectReviewDTO2.setId(2L);
        assertThat(projectReviewDTO1).isNotEqualTo(projectReviewDTO2);
        projectReviewDTO1.setId(null);
        assertThat(projectReviewDTO1).isNotEqualTo(projectReviewDTO2);
    }
}
