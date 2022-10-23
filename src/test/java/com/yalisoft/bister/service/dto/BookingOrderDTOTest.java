package com.yalisoft.bister.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yalisoft.bister.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookingOrderDTO.class);
        BookingOrderDTO bookingOrderDTO1 = new BookingOrderDTO();
        bookingOrderDTO1.setId(1L);
        BookingOrderDTO bookingOrderDTO2 = new BookingOrderDTO();
        assertThat(bookingOrderDTO1).isNotEqualTo(bookingOrderDTO2);
        bookingOrderDTO2.setId(bookingOrderDTO1.getId());
        assertThat(bookingOrderDTO1).isEqualTo(bookingOrderDTO2);
        bookingOrderDTO2.setId(2L);
        assertThat(bookingOrderDTO1).isNotEqualTo(bookingOrderDTO2);
        bookingOrderDTO1.setId(null);
        assertThat(bookingOrderDTO1).isNotEqualTo(bookingOrderDTO2);
    }
}
