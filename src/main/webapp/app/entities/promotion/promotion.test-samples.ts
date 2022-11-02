import dayjs from 'dayjs/esm';

import { PromotionContentType } from 'app/entities/enumerations/promotion-content-type.model';

import { IPromotion, NewPromotion } from './promotion.model';

export const sampleWithRequiredData: IPromotion = {
  id: 46788,
  contentType: PromotionContentType['ATTACHMENT_TEMPLATE'],
  createdAt: dayjs('2022-10-28T07:57'),
  sendAt: dayjs('2022-10-27T21:41'),
};

export const sampleWithPartialData: IPromotion = {
  id: 74768,
  productId: 36580,
  projectId: 71977,
  contentType: PromotionContentType['ATTACHMENT_TEMPLATE'],
  recipients: 'generate Keyboard Applications',
  createdBy: 85458,
  createdAt: dayjs('2022-10-27T16:24'),
  sendAt: dayjs('2022-10-28T07:08'),
};

export const sampleWithFullData: IPromotion = {
  id: 37530,
  productId: 54961,
  projectId: 67669,
  contentType: PromotionContentType['TEMPLATE'],
  recipients: 'firewall e-tailers',
  recipientGroup: 'core Arizona synthesizing',
  createdBy: 26439,
  createdAt: dayjs('2022-10-27T19:49'),
  sendAt: dayjs('2022-10-27T23:02'),
  attachmentId: 42050,
};

export const sampleWithNewData: NewPromotion = {
  contentType: PromotionContentType['TEMPLATE'],
  createdAt: dayjs('2022-10-28T13:37'),
  sendAt: dayjs('2022-10-28T09:15'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
