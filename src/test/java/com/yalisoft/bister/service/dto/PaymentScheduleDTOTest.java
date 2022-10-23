package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentScheduleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentScheduleDTO.class);
        PaymentScheduleDTO paymentScheduleDTO1 = new PaymentScheduleDTO();
        paymentScheduleDTO1.setId(1L);
        PaymentScheduleDTO paymentScheduleDTO2 = new PaymentScheduleDTO();
        assertThat(paymentScheduleDTO1).isNotEqualTo(paymentScheduleDTO2);
        paymentScheduleDTO2.setId(paymentScheduleDTO1.getId());
        assertThat(paymentScheduleDTO1).isEqualTo(paymentScheduleDTO2);
        paymentScheduleDTO2.setId(2L);
        assertThat(paymentScheduleDTO1).isNotEqualTo(paymentScheduleDTO2);
        paymentScheduleDTO1.setId(null);
        assertThat(paymentScheduleDTO1).isNotEqualTo(paymentScheduleDTO2);
    }
}
