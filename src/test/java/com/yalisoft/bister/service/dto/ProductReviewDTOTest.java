package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductReviewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductReviewDTO.class);
        ProductReviewDTO productReviewDTO1 = new ProductReviewDTO();
        productReviewDTO1.setId(1L);
        ProductReviewDTO productReviewDTO2 = new ProductReviewDTO();
        assertThat(productReviewDTO1).isNotEqualTo(productReviewDTO2);
        productReviewDTO2.setId(productReviewDTO1.getId());
        assertThat(productReviewDTO1).isEqualTo(productReviewDTO2);
        productReviewDTO2.setId(2L);
        assertThat(productReviewDTO1).isNotEqualTo(productReviewDTO2);
        productReviewDTO1.setId(null);
        assertThat(productReviewDTO1).isNotEqualTo(productReviewDTO2);
    }
}
