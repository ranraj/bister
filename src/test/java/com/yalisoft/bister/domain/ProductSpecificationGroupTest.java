package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductSpecificationGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductSpecificationGroup.class);
        ProductSpecificationGroup productSpecificationGroup1 = new ProductSpecificationGroup();
        productSpecificationGroup1.setId(1L);
        ProductSpecificationGroup productSpecificationGroup2 = new ProductSpecificationGroup();
        productSpecificationGroup2.setId(productSpecificationGroup1.getId());
        assertThat(productSpecificationGroup1).isEqualTo(productSpecificationGroup2);
        productSpecificationGroup2.setId(2L);
        assertThat(productSpecificationGroup1).isNotEqualTo(productSpecificationGroup2);
        productSpecificationGroup1.setId(null);
        assertThat(productSpecificationGroup1).isNotEqualTo(productSpecificationGroup2);
    }
}
