package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductAttributeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductAttribute.class);
        ProductAttribute productAttribute1 = new ProductAttribute();
        productAttribute1.setId(1L);
        ProductAttribute productAttribute2 = new ProductAttribute();
        productAttribute2.setId(productAttribute1.getId());
        assertThat(productAttribute1).isEqualTo(productAttribute2);
        productAttribute2.setId(2L);
        assertThat(productAttribute1).isNotEqualTo(productAttribute2);
        productAttribute1.setId(null);
        assertThat(productAttribute1).isNotEqualTo(productAttribute2);
    }
}
