package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentScheduleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentSchedule.class);
        PaymentSchedule paymentSchedule1 = new PaymentSchedule();
        paymentSchedule1.setId(1L);
        PaymentSchedule paymentSchedule2 = new PaymentSchedule();
        paymentSchedule2.setId(paymentSchedule1.getId());
        assertThat(paymentSchedule1).isEqualTo(paymentSchedule2);
        paymentSchedule2.setId(2L);
        assertThat(paymentSchedule1).isNotEqualTo(paymentSchedule2);
        paymentSchedule1.setId(null);
        assertThat(paymentSchedule1).isNotEqualTo(paymentSchedule2);
    }
}
