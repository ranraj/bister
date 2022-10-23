package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductSpecificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductSpecification.class);
        ProductSpecification productSpecification1 = new ProductSpecification();
        productSpecification1.setId(1L);
        ProductSpecification productSpecification2 = new ProductSpecification();
        productSpecification2.setId(productSpecification1.getId());
        assertThat(productSpecification1).isEqualTo(productSpecification2);
        productSpecification2.setId(2L);
        assertThat(productSpecification1).isNotEqualTo(productSpecification2);
        productSpecification1.setId(null);
        assertThat(productSpecification1).isNotEqualTo(productSpecification2);
    }
}
