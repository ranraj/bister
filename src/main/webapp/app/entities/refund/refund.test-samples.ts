import { RefundStatus } from 'app/entities/enumerations/refund-status.model';

import { IRefund, NewRefund } from './refund.model';

export const sampleWithRequiredData: IRefund = {
  id: 51513,
  amount: 35878,
  reason: 'withdrawal',
  orderCode: 67562,
  status: RefundStatus['PENDING'],
};

export const sampleWithPartialData: IRefund = {
  id: 31309,
  amount: 80597,
  reason: 'synergize Directives',
  orderCode: 83058,
  status: RefundStatus['PENDING'],
};

export const sampleWithFullData: IRefund = {
  id: 77448,
  amount: 91885,
  reason: 'Metal Web networks',
  orderCode: 20444,
  status: RefundStatus['COMPLETE'],
};

export const sampleWithNewData: NewRefund = {
  amount: 94487,
  reason: 'Borders invoice',
  orderCode: 8216,
  status: RefundStatus['PENDING'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
