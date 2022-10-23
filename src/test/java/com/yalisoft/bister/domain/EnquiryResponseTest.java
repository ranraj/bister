package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnquiryResponseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnquiryResponse.class);
        EnquiryResponse enquiryResponse1 = new EnquiryResponse();
        enquiryResponse1.setId(1L);
        EnquiryResponse enquiryResponse2 = new EnquiryResponse();
        enquiryResponse2.setId(enquiryResponse1.getId());
        assertThat(enquiryResponse1).isEqualTo(enquiryResponse2);
        enquiryResponse2.setId(2L);
        assertThat(enquiryResponse1).isNotEqualTo(enquiryResponse2);
        enquiryResponse1.setId(null);
        assertThat(enquiryResponse1).isNotEqualTo(enquiryResponse2);
    }
}
