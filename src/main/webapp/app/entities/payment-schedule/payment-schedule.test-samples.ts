import dayjs from 'dayjs/esm';

import { PaymentScheduleStatus } from 'app/entities/enumerations/payment-schedule-status.model';

import { IPaymentSchedule, NewPaymentSchedule } from './payment-schedule.model';

export const sampleWithRequiredData: IPaymentSchedule = {
  id: 74449,
  dueDate: dayjs('2022-10-22T17:20'),
  totalPrice: 86665,
  status: PaymentScheduleStatus['PENDING'],
};

export const sampleWithPartialData: IPaymentSchedule = {
  id: 14951,
  dueDate: dayjs('2022-10-22T08:59'),
  totalPrice: 50486,
  remarks: 'alarm paymentXXXXXXX',
  status: PaymentScheduleStatus['PAID'],
};

export const sampleWithFullData: IPaymentSchedule = {
  id: 9322,
  dueDate: dayjs('2022-10-22T05:46'),
  totalPrice: 53346,
  remarks: 'HDDXXXXXXXXXXXXXXXXX',
  status: PaymentScheduleStatus['PENDING'],
  isOverDue: true,
};

export const sampleWithNewData: NewPaymentSchedule = {
  dueDate: dayjs('2022-10-22T14:27'),
  totalPrice: 28245,
  status: PaymentScheduleStatus['PAID'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
