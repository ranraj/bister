package com.yalisoft.bister.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookingOrder.class);
        BookingOrder bookingOrder1 = new BookingOrder();
        bookingOrder1.setId(1L);
        BookingOrder bookingOrder2 = new BookingOrder();
        bookingOrder2.setId(bookingOrder1.getId());
        assertThat(bookingOrder1).isEqualTo(bookingOrder2);
        bookingOrder2.setId(2L);
        assertThat(bookingOrder1).isNotEqualTo(bookingOrder2);
        bookingOrder1.setId(null);
        assertThat(bookingOrder1).isNotEqualTo(bookingOrder2);
    }
}
