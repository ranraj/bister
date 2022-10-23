package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnquiryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Enquiry.class);
        Enquiry enquiry1 = new Enquiry();
        enquiry1.setId(1L);
        Enquiry enquiry2 = new Enquiry();
        enquiry2.setId(enquiry1.getId());
        assertThat(enquiry1).isEqualTo(enquiry2);
        enquiry2.setId(2L);
        assertThat(enquiry1).isNotEqualTo(enquiry2);
        enquiry1.setId(null);
        assertThat(enquiry1).isNotEqualTo(enquiry2);
    }
}
