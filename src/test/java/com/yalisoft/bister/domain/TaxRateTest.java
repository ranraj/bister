package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaxRateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaxRate.class);
        TaxRate taxRate1 = new TaxRate();
        taxRate1.setId(1L);
        TaxRate taxRate2 = new TaxRate();
        taxRate2.setId(taxRate1.getId());
        assertThat(taxRate1).isEqualTo(taxRate2);
        taxRate2.setId(2L);
        assertThat(taxRate1).isNotEqualTo(taxRate2);
        taxRate1.setId(null);
        assertThat(taxRate1).isNotEqualTo(taxRate2);
    }
}
