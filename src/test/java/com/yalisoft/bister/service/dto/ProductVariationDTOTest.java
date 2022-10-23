package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductVariationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductVariationDTO.class);
        ProductVariationDTO productVariationDTO1 = new ProductVariationDTO();
        productVariationDTO1.setId(1L);
        ProductVariationDTO productVariationDTO2 = new ProductVariationDTO();
        assertThat(productVariationDTO1).isNotEqualTo(productVariationDTO2);
        productVariationDTO2.setId(productVariationDTO1.getId());
        assertThat(productVariationDTO1).isEqualTo(productVariationDTO2);
        productVariationDTO2.setId(2L);
        assertThat(productVariationDTO1).isNotEqualTo(productVariationDTO2);
        productVariationDTO1.setId(null);
        assertThat(productVariationDTO1).isNotEqualTo(productVariationDTO2);
    }
}
