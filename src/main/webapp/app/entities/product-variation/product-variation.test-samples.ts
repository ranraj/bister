import dayjs from 'dayjs/esm';

import { SaleStatus } from 'app/entities/enumerations/sale-status.model';

import { IProductVariation, NewProductVariation } from './product-variation.model';

export const sampleWithRequiredData: IProductVariation = {
  id: 26195,
  name: 'fresh-thinking Licensed',
  description: 'circuitXXX',
  regularPrice: 11150,
  salePrice: 83504,
  dateOnSaleFrom: dayjs('2022-10-27'),
  dateOnSaleTo: dayjs('2022-10-28'),
  isDraft: true,
  useParentDetails: false,
  saleStatus: SaleStatus['DELIVERY'],
};

export const sampleWithPartialData: IProductVariation = {
  id: 93746,
  name: 'Principal SMS',
  description: 'CarXXXXXXX',
  regularPrice: 21914,
  salePrice: 38484,
  dateOnSaleFrom: dayjs('2022-10-28'),
  dateOnSaleTo: dayjs('2022-10-27'),
  isDraft: true,
  useParentDetails: false,
  saleStatus: SaleStatus['RESALE'],
};

export const sampleWithFullData: IProductVariation = {
  id: 62985,
  assetId: 'feed protocol wireless',
  name: 'Portugal',
  description: 'Refined parse',
  regularPrice: 36990,
  salePrice: 72748,
  dateOnSaleFrom: dayjs('2022-10-27'),
  dateOnSaleTo: dayjs('2022-10-27'),
  isDraft: false,
  useParentDetails: true,
  saleStatus: SaleStatus['SOLD'],
};

export const sampleWithNewData: NewProductVariation = {
  name: 'Producer',
  description: 'ChiefXXXXX',
  regularPrice: 66221,
  salePrice: 30715,
  dateOnSaleFrom: dayjs('2022-10-28'),
  dateOnSaleTo: dayjs('2022-10-27'),
  isDraft: true,
  useParentDetails: false,
  saleStatus: SaleStatus['RESALE'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
