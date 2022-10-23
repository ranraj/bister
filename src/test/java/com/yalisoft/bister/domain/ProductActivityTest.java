package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductActivityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductActivity.class);
        ProductActivity productActivity1 = new ProductActivity();
        productActivity1.setId(1L);
        ProductActivity productActivity2 = new ProductActivity();
        productActivity2.setId(productActivity1.getId());
        assertThat(productActivity1).isEqualTo(productActivity2);
        productActivity2.setId(2L);
        assertThat(productActivity1).isNotEqualTo(productActivity2);
        productActivity1.setId(null);
        assertThat(productActivity1).isNotEqualTo(productActivity2);
    }
}
