package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductAttributeTermDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductAttributeTermDTO.class);
        ProductAttributeTermDTO productAttributeTermDTO1 = new ProductAttributeTermDTO();
        productAttributeTermDTO1.setId(1L);
        ProductAttributeTermDTO productAttributeTermDTO2 = new ProductAttributeTermDTO();
        assertThat(productAttributeTermDTO1).isNotEqualTo(productAttributeTermDTO2);
        productAttributeTermDTO2.setId(productAttributeTermDTO1.getId());
        assertThat(productAttributeTermDTO1).isEqualTo(productAttributeTermDTO2);
        productAttributeTermDTO2.setId(2L);
        assertThat(productAttributeTermDTO1).isNotEqualTo(productAttributeTermDTO2);
        productAttributeTermDTO1.setId(null);
        assertThat(productAttributeTermDTO1).isNotEqualTo(productAttributeTermDTO2);
    }
}
