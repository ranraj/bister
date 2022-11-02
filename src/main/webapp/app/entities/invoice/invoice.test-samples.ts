import dayjs from 'dayjs/esm';

import { InvoiceStatus } from 'app/entities/enumerations/invoice-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

import { IInvoice, NewInvoice } from './invoice.model';

export const sampleWithRequiredData: IInvoice = {
  id: 91509,
  code: 'Avon Mandatory',
  date: dayjs('2022-10-28T10:27'),
  status: InvoiceStatus['ISSUED'],
  paymentMethod: PaymentMethod['CREDIT_CARD'],
  paymentDate: dayjs('2022-10-28T04:06'),
  paymentAmount: 13016,
};

export const sampleWithPartialData: IInvoice = {
  id: 32294,
  code: 'Tugrik Customer schemas',
  date: dayjs('2022-10-28T06:34'),
  details: 'Regional Optimization',
  status: InvoiceStatus['PAID'],
  paymentMethod: PaymentMethod['CREDIT_CARD'],
  paymentDate: dayjs('2022-10-28T10:54'),
  paymentAmount: 69991,
};

export const sampleWithFullData: IInvoice = {
  id: 13401,
  code: 'Account',
  date: dayjs('2022-10-28T06:22'),
  details: 'Creative Intelligent Berkshire',
  status: InvoiceStatus['PAID'],
  paymentMethod: PaymentMethod['CASH'],
  paymentDate: dayjs('2022-10-28T12:15'),
  paymentAmount: 79094,
};

export const sampleWithNewData: NewInvoice = {
  code: 'Bedfordshire online application',
  date: dayjs('2022-10-27T23:41'),
  status: InvoiceStatus['CANCELLED'],
  paymentMethod: PaymentMethod['CASH'],
  paymentDate: dayjs('2022-10-27T15:18'),
  paymentAmount: 58958,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
