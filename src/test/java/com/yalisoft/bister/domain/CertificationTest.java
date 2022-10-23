package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CertificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Certification.class);
        Certification certification1 = new Certification();
        certification1.setId(1L);
        Certification certification2 = new Certification();
        certification2.setId(certification1.getId());
        assertThat(certification1).isEqualTo(certification2);
        certification2.setId(2L);
        assertThat(certification1).isNotEqualTo(certification2);
        certification1.setId(null);
        assertThat(certification1).isNotEqualTo(certification2);
    }
}
