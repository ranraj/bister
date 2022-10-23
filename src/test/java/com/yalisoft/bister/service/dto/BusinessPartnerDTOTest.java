package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BusinessPartnerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessPartnerDTO.class);
        BusinessPartnerDTO businessPartnerDTO1 = new BusinessPartnerDTO();
        businessPartnerDTO1.setId(1L);
        BusinessPartnerDTO businessPartnerDTO2 = new BusinessPartnerDTO();
        assertThat(businessPartnerDTO1).isNotEqualTo(businessPartnerDTO2);
        businessPartnerDTO2.setId(businessPartnerDTO1.getId());
        assertThat(businessPartnerDTO1).isEqualTo(businessPartnerDTO2);
        businessPartnerDTO2.setId(2L);
        assertThat(businessPartnerDTO1).isNotEqualTo(businessPartnerDTO2);
        businessPartnerDTO1.setId(null);
        assertThat(businessPartnerDTO1).isNotEqualTo(businessPartnerDTO2);
    }
}
