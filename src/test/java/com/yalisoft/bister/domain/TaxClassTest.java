package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaxClassTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaxClass.class);
        TaxClass taxClass1 = new TaxClass();
        taxClass1.setId(1L);
        TaxClass taxClass2 = new TaxClass();
        taxClass2.setId(taxClass1.getId());
        assertThat(taxClass1).isEqualTo(taxClass2);
        taxClass2.setId(2L);
        assertThat(taxClass1).isNotEqualTo(taxClass2);
        taxClass1.setId(null);
        assertThat(taxClass1).isNotEqualTo(taxClass2);
    }
}
