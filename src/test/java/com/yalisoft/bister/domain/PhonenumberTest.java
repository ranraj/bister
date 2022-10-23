package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PhonenumberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Phonenumber.class);
        Phonenumber phonenumber1 = new Phonenumber();
        phonenumber1.setId(1L);
        Phonenumber phonenumber2 = new Phonenumber();
        phonenumber2.setId(phonenumber1.getId());
        assertThat(phonenumber1).isEqualTo(phonenumber2);
        phonenumber2.setId(2L);
        assertThat(phonenumber1).isNotEqualTo(phonenumber2);
        phonenumber1.setId(null);
        assertThat(phonenumber1).isNotEqualTo(phonenumber2);
    }
}
