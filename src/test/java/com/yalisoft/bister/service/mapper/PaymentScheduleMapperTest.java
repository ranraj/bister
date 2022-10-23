package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentScheduleMapperTest {

    private PaymentScheduleMapper paymentScheduleMapper;

    @BeforeEach
    public void setUp() {
        paymentScheduleMapper = new PaymentScheduleMapperImpl();
    }
}
