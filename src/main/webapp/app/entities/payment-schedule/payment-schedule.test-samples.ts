import dayjs from 'dayjs/esm';

import { PaymentScheduleStatus } from 'app/entities/enumerations/payment-schedule-status.model';

import { IPaymentSchedule, NewPaymentSchedule } from './payment-schedule.model';

export const sampleWithRequiredData: IPaymentSchedule = {
  id: 74449,
  dueDate: dayjs('2022-10-28T03:56'),
  totalPrice: 86665,
  status: PaymentScheduleStatus['PENDING'],
};

export const sampleWithPartialData: IPaymentSchedule = {
  id: 14951,
  dueDate: dayjs('2022-10-27T19:35'),
  totalPrice: 50486,
  remarks: 'alarm paymentXXXXXXX',
  status: PaymentScheduleStatus['PAID'],
};

export const sampleWithFullData: IPaymentSchedule = {
  id: 9322,
  dueDate: dayjs('2022-10-27T16:22'),
  totalPrice: 53346,
  remarks: 'HDDXXXXXXXXXXXXXXXXX',
  status: PaymentScheduleStatus['PENDING'],
  isOverDue: true,
};

export const sampleWithNewData: NewPaymentSchedule = {
  dueDate: dayjs('2022-10-28T01:03'),
  totalPrice: 28245,
  status: PaymentScheduleStatus['PAID'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
