package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductVariationAttributeTermDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductVariationAttributeTermDTO.class);
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO1 = new ProductVariationAttributeTermDTO();
        productVariationAttributeTermDTO1.setId(1L);
        ProductVariationAttributeTermDTO productVariationAttributeTermDTO2 = new ProductVariationAttributeTermDTO();
        assertThat(productVariationAttributeTermDTO1).isNotEqualTo(productVariationAttributeTermDTO2);
        productVariationAttributeTermDTO2.setId(productVariationAttributeTermDTO1.getId());
        assertThat(productVariationAttributeTermDTO1).isEqualTo(productVariationAttributeTermDTO2);
        productVariationAttributeTermDTO2.setId(2L);
        assertThat(productVariationAttributeTermDTO1).isNotEqualTo(productVariationAttributeTermDTO2);
        productVariationAttributeTermDTO1.setId(null);
        assertThat(productVariationAttributeTermDTO1).isNotEqualTo(productVariationAttributeTermDTO2);
    }
}
