import dayjs from 'dayjs/esm';

import { BookingOrderStatus } from 'app/entities/enumerations/booking-order-status.model';

import { IBookingOrder, NewBookingOrder } from './booking-order.model';

export const sampleWithRequiredData: IBookingOrder = {
  id: 90821,
  placedDate: dayjs('2022-10-27T22:18'),
  status: BookingOrderStatus['BLOCKED'],
};

export const sampleWithPartialData: IBookingOrder = {
  id: 34486,
  placedDate: dayjs('2022-10-27T21:28'),
  status: BookingOrderStatus['BOOKED'],
  code: 'Small compress Fresh',
};

export const sampleWithFullData: IBookingOrder = {
  id: 60171,
  placedDate: dayjs('2022-10-27T22:17'),
  status: BookingOrderStatus['EXPIRED'],
  code: 'BuckinghamshireXXXXX',
  bookingExpieryDate: dayjs('2022-10-27T20:30'),
};

export const sampleWithNewData: NewBookingOrder = {
  placedDate: dayjs('2022-10-27T17:18'),
  status: BookingOrderStatus['EXPIRED'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
