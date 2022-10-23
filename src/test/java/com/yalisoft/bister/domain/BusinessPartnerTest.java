package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BusinessPartnerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessPartner.class);
        BusinessPartner businessPartner1 = new BusinessPartner();
        businessPartner1.setId(1L);
        BusinessPartner businessPartner2 = new BusinessPartner();
        businessPartner2.setId(businessPartner1.getId());
        assertThat(businessPartner1).isEqualTo(businessPartner2);
        businessPartner2.setId(2L);
        assertThat(businessPartner1).isNotEqualTo(businessPartner2);
        businessPartner1.setId(null);
        assertThat(businessPartner1).isNotEqualTo(businessPartner2);
    }
}
