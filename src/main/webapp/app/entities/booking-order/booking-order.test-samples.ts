import dayjs from 'dayjs/esm';

import { BookingOrderStatus } from 'app/entities/enumerations/booking-order-status.model';

import { IBookingOrder, NewBookingOrder } from './booking-order.model';

export const sampleWithRequiredData: IBookingOrder = {
  id: 90821,
  placedDate: dayjs('2022-10-22T11:42'),
  status: BookingOrderStatus['BLOCKED'],
};

export const sampleWithPartialData: IBookingOrder = {
  id: 34486,
  placedDate: dayjs('2022-10-22T10:52'),
  status: BookingOrderStatus['BOOKED'],
  code: 'Small compress Fresh',
};

export const sampleWithFullData: IBookingOrder = {
  id: 60171,
  placedDate: dayjs('2022-10-22T11:41'),
  status: BookingOrderStatus['EXPIRED'],
  code: 'BuckinghamshireXXXXX',
  bookingExpieryDate: dayjs('2022-10-22T09:54'),
};

export const sampleWithNewData: NewBookingOrder = {
  placedDate: dayjs('2022-10-22T06:42'),
  status: BookingOrderStatus['EXPIRED'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
