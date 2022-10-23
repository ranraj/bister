package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductSpecificationGroupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductSpecificationGroupDTO.class);
        ProductSpecificationGroupDTO productSpecificationGroupDTO1 = new ProductSpecificationGroupDTO();
        productSpecificationGroupDTO1.setId(1L);
        ProductSpecificationGroupDTO productSpecificationGroupDTO2 = new ProductSpecificationGroupDTO();
        assertThat(productSpecificationGroupDTO1).isNotEqualTo(productSpecificationGroupDTO2);
        productSpecificationGroupDTO2.setId(productSpecificationGroupDTO1.getId());
        assertThat(productSpecificationGroupDTO1).isEqualTo(productSpecificationGroupDTO2);
        productSpecificationGroupDTO2.setId(2L);
        assertThat(productSpecificationGroupDTO1).isNotEqualTo(productSpecificationGroupDTO2);
        productSpecificationGroupDTO1.setId(null);
        assertThat(productSpecificationGroupDTO1).isNotEqualTo(productSpecificationGroupDTO2);
    }
}
