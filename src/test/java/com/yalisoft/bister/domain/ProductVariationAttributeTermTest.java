package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductVariationAttributeTermTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductVariationAttributeTerm.class);
        ProductVariationAttributeTerm productVariationAttributeTerm1 = new ProductVariationAttributeTerm();
        productVariationAttributeTerm1.setId(1L);
        ProductVariationAttributeTerm productVariationAttributeTerm2 = new ProductVariationAttributeTerm();
        productVariationAttributeTerm2.setId(productVariationAttributeTerm1.getId());
        assertThat(productVariationAttributeTerm1).isEqualTo(productVariationAttributeTerm2);
        productVariationAttributeTerm2.setId(2L);
        assertThat(productVariationAttributeTerm1).isNotEqualTo(productVariationAttributeTerm2);
        productVariationAttributeTerm1.setId(null);
        assertThat(productVariationAttributeTerm1).isNotEqualTo(productVariationAttributeTerm2);
    }
}
