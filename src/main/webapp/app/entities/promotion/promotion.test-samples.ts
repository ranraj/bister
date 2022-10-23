import dayjs from 'dayjs/esm';

import { PromotionContentType } from 'app/entities/enumerations/promotion-content-type.model';

import { IPromotion, NewPromotion } from './promotion.model';

export const sampleWithRequiredData: IPromotion = {
  id: 46788,
  contentType: PromotionContentType['ATTACHMENT_TEMPLATE'],
  createdAt: dayjs('2022-10-22T21:21'),
  sendAt: dayjs('2022-10-22T11:05'),
};

export const sampleWithPartialData: IPromotion = {
  id: 74768,
  productId: 36580,
  projectId: 71977,
  contentType: PromotionContentType['ATTACHMENT_TEMPLATE'],
  recipients: 'generate Keyboard Applications',
  createdBy: 85458,
  createdAt: dayjs('2022-10-22T05:48'),
  sendAt: dayjs('2022-10-22T20:32'),
};

export const sampleWithFullData: IPromotion = {
  id: 37530,
  productId: 54961,
  projectId: 67669,
  contentType: PromotionContentType['TEMPLATE'],
  recipients: 'firewall e-tailers',
  recipientGroup: 'core Arizona synthesizing',
  createdBy: 26439,
  createdAt: dayjs('2022-10-22T09:13'),
  sendAt: dayjs('2022-10-22T12:26'),
  attachmentId: 42050,
};

export const sampleWithNewData: NewPromotion = {
  contentType: PromotionContentType['TEMPLATE'],
  createdAt: dayjs('2022-10-23T03:01'),
  sendAt: dayjs('2022-10-22T22:39'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
