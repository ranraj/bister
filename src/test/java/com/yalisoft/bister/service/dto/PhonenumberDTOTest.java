package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PhonenumberDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PhonenumberDTO.class);
        PhonenumberDTO phonenumberDTO1 = new PhonenumberDTO();
        phonenumberDTO1.setId(1L);
        PhonenumberDTO phonenumberDTO2 = new PhonenumberDTO();
        assertThat(phonenumberDTO1).isNotEqualTo(phonenumberDTO2);
        phonenumberDTO2.setId(phonenumberDTO1.getId());
        assertThat(phonenumberDTO1).isEqualTo(phonenumberDTO2);
        phonenumberDTO2.setId(2L);
        assertThat(phonenumberDTO1).isNotEqualTo(phonenumberDTO2);
        phonenumberDTO1.setId(null);
        assertThat(phonenumberDTO1).isNotEqualTo(phonenumberDTO2);
    }
}
