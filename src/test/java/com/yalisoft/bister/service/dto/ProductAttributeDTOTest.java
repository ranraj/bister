package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductAttributeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductAttributeDTO.class);
        ProductAttributeDTO productAttributeDTO1 = new ProductAttributeDTO();
        productAttributeDTO1.setId(1L);
        ProductAttributeDTO productAttributeDTO2 = new ProductAttributeDTO();
        assertThat(productAttributeDTO1).isNotEqualTo(productAttributeDTO2);
        productAttributeDTO2.setId(productAttributeDTO1.getId());
        assertThat(productAttributeDTO1).isEqualTo(productAttributeDTO2);
        productAttributeDTO2.setId(2L);
        assertThat(productAttributeDTO1).isNotEqualTo(productAttributeDTO2);
        productAttributeDTO1.setId(null);
        assertThat(productAttributeDTO1).isNotEqualTo(productAttributeDTO2);
    }
}
