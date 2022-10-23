package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnquiryResponseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnquiryResponseDTO.class);
        EnquiryResponseDTO enquiryResponseDTO1 = new EnquiryResponseDTO();
        enquiryResponseDTO1.setId(1L);
        EnquiryResponseDTO enquiryResponseDTO2 = new EnquiryResponseDTO();
        assertThat(enquiryResponseDTO1).isNotEqualTo(enquiryResponseDTO2);
        enquiryResponseDTO2.setId(enquiryResponseDTO1.getId());
        assertThat(enquiryResponseDTO1).isEqualTo(enquiryResponseDTO2);
        enquiryResponseDTO2.setId(2L);
        assertThat(enquiryResponseDTO1).isNotEqualTo(enquiryResponseDTO2);
        enquiryResponseDTO1.setId(null);
        assertThat(enquiryResponseDTO1).isNotEqualTo(enquiryResponseDTO2);
    }
}
