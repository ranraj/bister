package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RefundDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RefundDTO.class);
        RefundDTO refundDTO1 = new RefundDTO();
        refundDTO1.setId(1L);
        RefundDTO refundDTO2 = new RefundDTO();
        assertThat(refundDTO1).isNotEqualTo(refundDTO2);
        refundDTO2.setId(refundDTO1.getId());
        assertThat(refundDTO1).isEqualTo(refundDTO2);
        refundDTO2.setId(2L);
        assertThat(refundDTO1).isNotEqualTo(refundDTO2);
        refundDTO1.setId(null);
        assertThat(refundDTO1).isNotEqualTo(refundDTO2);
    }
}
