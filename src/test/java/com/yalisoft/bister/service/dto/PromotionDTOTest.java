package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PromotionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PromotionDTO.class);
        PromotionDTO promotionDTO1 = new PromotionDTO();
        promotionDTO1.setId(1L);
        PromotionDTO promotionDTO2 = new PromotionDTO();
        assertThat(promotionDTO1).isNotEqualTo(promotionDTO2);
        promotionDTO2.setId(promotionDTO1.getId());
        assertThat(promotionDTO1).isEqualTo(promotionDTO2);
        promotionDTO2.setId(2L);
        assertThat(promotionDTO1).isNotEqualTo(promotionDTO2);
        promotionDTO1.setId(null);
        assertThat(promotionDTO1).isNotEqualTo(promotionDTO2);
    }
}
