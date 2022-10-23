package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductActivityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductActivityDTO.class);
        ProductActivityDTO productActivityDTO1 = new ProductActivityDTO();
        productActivityDTO1.setId(1L);
        ProductActivityDTO productActivityDTO2 = new ProductActivityDTO();
        assertThat(productActivityDTO1).isNotEqualTo(productActivityDTO2);
        productActivityDTO2.setId(productActivityDTO1.getId());
        assertThat(productActivityDTO1).isEqualTo(productActivityDTO2);
        productActivityDTO2.setId(2L);
        assertThat(productActivityDTO1).isNotEqualTo(productActivityDTO2);
        productActivityDTO1.setId(null);
        assertThat(productActivityDTO1).isNotEqualTo(productActivityDTO2);
    }
}
