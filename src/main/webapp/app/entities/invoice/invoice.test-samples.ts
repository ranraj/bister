import dayjs from 'dayjs/esm';

import { InvoiceStatus } from 'app/entities/enumerations/invoice-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

import { IInvoice, NewInvoice } from './invoice.model';

export const sampleWithRequiredData: IInvoice = {
  id: 91509,
  code: 'Avon Mandatory',
  date: dayjs('2022-10-22T23:51'),
  status: InvoiceStatus['ISSUED'],
  paymentMethod: PaymentMethod['CREDIT_CARD'],
  paymentDate: dayjs('2022-10-22T17:30'),
  paymentAmount: 13016,
};

export const sampleWithPartialData: IInvoice = {
  id: 32294,
  code: 'Tugrik Customer schemas',
  date: dayjs('2022-10-22T19:58'),
  details: 'Regional Optimization',
  status: InvoiceStatus['PAID'],
  paymentMethod: PaymentMethod['CREDIT_CARD'],
  paymentDate: dayjs('2022-10-23T00:17'),
  paymentAmount: 69991,
};

export const sampleWithFullData: IInvoice = {
  id: 13401,
  code: 'Account',
  date: dayjs('2022-10-22T19:46'),
  details: 'Creative Intelligent Berkshire',
  status: InvoiceStatus['PAID'],
  paymentMethod: PaymentMethod['CASH'],
  paymentDate: dayjs('2022-10-23T01:39'),
  paymentAmount: 79094,
};

export const sampleWithNewData: NewInvoice = {
  code: 'Bedfordshire online application',
  date: dayjs('2022-10-22T13:05'),
  status: InvoiceStatus['CANCELLED'],
  paymentMethod: PaymentMethod['CASH'],
  paymentDate: dayjs('2022-10-22T04:42'),
  paymentAmount: 58958,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
