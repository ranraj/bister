package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaxClassDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaxClassDTO.class);
        TaxClassDTO taxClassDTO1 = new TaxClassDTO();
        taxClassDTO1.setId(1L);
        TaxClassDTO taxClassDTO2 = new TaxClassDTO();
        assertThat(taxClassDTO1).isNotEqualTo(taxClassDTO2);
        taxClassDTO2.setId(taxClassDTO1.getId());
        assertThat(taxClassDTO1).isEqualTo(taxClassDTO2);
        taxClassDTO2.setId(2L);
        assertThat(taxClassDTO1).isNotEqualTo(taxClassDTO2);
        taxClassDTO1.setId(null);
        assertThat(taxClassDTO1).isNotEqualTo(taxClassDTO2);
    }
}
