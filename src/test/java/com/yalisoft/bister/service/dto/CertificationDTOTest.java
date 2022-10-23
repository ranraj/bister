package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CertificationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CertificationDTO.class);
        CertificationDTO certificationDTO1 = new CertificationDTO();
        certificationDTO1.setId(1L);
        CertificationDTO certificationDTO2 = new CertificationDTO();
        assertThat(certificationDTO1).isNotEqualTo(certificationDTO2);
        certificationDTO2.setId(certificationDTO1.getId());
        assertThat(certificationDTO1).isEqualTo(certificationDTO2);
        certificationDTO2.setId(2L);
        assertThat(certificationDTO1).isNotEqualTo(certificationDTO2);
        certificationDTO1.setId(null);
        assertThat(certificationDTO1).isNotEqualTo(certificationDTO2);
    }
}
