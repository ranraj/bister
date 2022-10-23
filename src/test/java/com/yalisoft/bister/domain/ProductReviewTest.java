package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductReview.class);
        ProductReview productReview1 = new ProductReview();
        productReview1.setId(1L);
        ProductReview productReview2 = new ProductReview();
        productReview2.setId(productReview1.getId());
        assertThat(productReview1).isEqualTo(productReview2);
        productReview2.setId(2L);
        assertThat(productReview1).isNotEqualTo(productReview2);
        productReview1.setId(null);
        assertThat(productReview1).isNotEqualTo(productReview2);
    }
}
