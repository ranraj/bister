package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnquiryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnquiryDTO.class);
        EnquiryDTO enquiryDTO1 = new EnquiryDTO();
        enquiryDTO1.setId(1L);
        EnquiryDTO enquiryDTO2 = new EnquiryDTO();
        assertThat(enquiryDTO1).isNotEqualTo(enquiryDTO2);
        enquiryDTO2.setId(enquiryDTO1.getId());
        assertThat(enquiryDTO1).isEqualTo(enquiryDTO2);
        enquiryDTO2.setId(2L);
        assertThat(enquiryDTO1).isNotEqualTo(enquiryDTO2);
        enquiryDTO1.setId(null);
        assertThat(enquiryDTO1).isNotEqualTo(enquiryDTO2);
    }
}
