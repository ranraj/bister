package com.yalisoft.bister.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingOrderMapperTest {

    private BookingOrderMapper bookingOrderMapper;

    @BeforeEach
    public void setUp() {
        bookingOrderMapper = new BookingOrderMapperImpl();
    }
}
