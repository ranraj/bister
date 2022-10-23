package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductVariationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductVariation.class);
        ProductVariation productVariation1 = new ProductVariation();
        productVariation1.setId(1L);
        ProductVariation productVariation2 = new ProductVariation();
        productVariation2.setId(productVariation1.getId());
        assertThat(productVariation1).isEqualTo(productVariation2);
        productVariation2.setId(2L);
        assertThat(productVariation1).isNotEqualTo(productVariation2);
        productVariation1.setId(null);
        assertThat(productVariation1).isNotEqualTo(productVariation2);
    }
}
