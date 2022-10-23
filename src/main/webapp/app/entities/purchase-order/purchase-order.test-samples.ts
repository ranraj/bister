import dayjs from 'dayjs/esm';

import { OrderStatus } from 'app/entities/enumerations/order-status.model';
import { DeliveryOption } from 'app/entities/enumerations/delivery-option.model';

import { IPurchaseOrder, NewPurchaseOrder } from './purchase-order.model';

export const sampleWithRequiredData: IPurchaseOrder = {
  id: 1775,
  placedDate: dayjs('2022-10-22T12:44'),
  status: OrderStatus['PAYMENT_PENDING'],
  deliveryOption: DeliveryOption['RENT'],
};

export const sampleWithPartialData: IPurchaseOrder = {
  id: 20490,
  placedDate: dayjs('2022-10-22T17:30'),
  status: OrderStatus['PAYMENT_PENDING'],
  code: 'high-level skyXXXXXX',
  deliveryOption: DeliveryOption['RENT'],
};

export const sampleWithFullData: IPurchaseOrder = {
  id: 9779,
  placedDate: dayjs('2022-10-22T23:06'),
  status: OrderStatus['DELIVERED'],
  code: 'MarylandXXXXXXXXXXXX',
  deliveryOption: DeliveryOption['LEASE'],
};

export const sampleWithNewData: NewPurchaseOrder = {
  placedDate: dayjs('2022-10-22T19:06'),
  status: OrderStatus['ORDER_CONFIMED'],
  deliveryOption: DeliveryOption['HAND_OVER'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
