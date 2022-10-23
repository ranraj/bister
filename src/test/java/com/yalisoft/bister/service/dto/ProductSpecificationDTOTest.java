package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductSpecificationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductSpecificationDTO.class);
        ProductSpecificationDTO productSpecificationDTO1 = new ProductSpecificationDTO();
        productSpecificationDTO1.setId(1L);
        ProductSpecificationDTO productSpecificationDTO2 = new ProductSpecificationDTO();
        assertThat(productSpecificationDTO1).isNotEqualTo(productSpecificationDTO2);
        productSpecificationDTO2.setId(productSpecificationDTO1.getId());
        assertThat(productSpecificationDTO1).isEqualTo(productSpecificationDTO2);
        productSpecificationDTO2.setId(2L);
        assertThat(productSpecificationDTO1).isNotEqualTo(productSpecificationDTO2);
        productSpecificationDTO1.setId(null);
        assertThat(productSpecificationDTO1).isNotEqualTo(productSpecificationDTO2);
    }
}
