package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectReview.class);
        ProjectReview projectReview1 = new ProjectReview();
        projectReview1.setId(1L);
        ProjectReview projectReview2 = new ProjectReview();
        projectReview2.setId(projectReview1.getId());
        assertThat(projectReview1).isEqualTo(projectReview2);
        projectReview2.setId(2L);
        assertThat(projectReview1).isNotEqualTo(projectReview2);
        projectReview1.setId(null);
        assertThat(projectReview1).isNotEqualTo(projectReview2);
    }
}
