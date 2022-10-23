package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaxRateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaxRateDTO.class);
        TaxRateDTO taxRateDTO1 = new TaxRateDTO();
        taxRateDTO1.setId(1L);
        TaxRateDTO taxRateDTO2 = new TaxRateDTO();
        assertThat(taxRateDTO1).isNotEqualTo(taxRateDTO2);
        taxRateDTO2.setId(taxRateDTO1.getId());
        assertThat(taxRateDTO1).isEqualTo(taxRateDTO2);
        taxRateDTO2.setId(2L);
        assertThat(taxRateDTO1).isNotEqualTo(taxRateDTO2);
        taxRateDTO1.setId(null);
        assertThat(taxRateDTO1).isNotEqualTo(taxRateDTO2);
    }
}
