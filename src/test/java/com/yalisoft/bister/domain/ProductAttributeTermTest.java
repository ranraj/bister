package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductAttributeTermTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductAttributeTerm.class);
        ProductAttributeTerm productAttributeTerm1 = new ProductAttributeTerm();
        productAttributeTerm1.setId(1L);
        ProductAttributeTerm productAttributeTerm2 = new ProductAttributeTerm();
        productAttributeTerm2.setId(productAttributeTerm1.getId());
        assertThat(productAttributeTerm1).isEqualTo(productAttributeTerm2);
        productAttributeTerm2.setId(2L);
        assertThat(productAttributeTerm1).isNotEqualTo(productAttributeTerm2);
        productAttributeTerm1.setId(null);
        assertThat(productAttributeTerm1).isNotEqualTo(productAttributeTerm2);
    }
}
