import dayjs from 'dayjs/esm';

import { SaleStatus } from 'app/entities/enumerations/sale-status.model';

import { IProduct, NewProduct } from './product.model';

export const sampleWithRequiredData: IProduct = {
  id: 77672,
  name: 'Customer-focused',
  slug: 'cross-platform Unbranded deposit',
  description: 'Interface TableXXXXX',
  shortDescription: 'StravenueXXXXXXXXXXX',
  regularPrice: 66440,
  salePrice: 85387,
  published: true,
  dateCreated: dayjs('2022-10-27T19:20'),
  dateModified: dayjs('2022-10-27'),
  featured: false,
  saleStatus: SaleStatus['CLOSED'],
};

export const sampleWithPartialData: IProduct = {
  id: 92938,
  name: 'Garden Customer-focused Island',
  slug: 'mission-critical',
  description: 'silverXXXXXXXXXXXXXX',
  shortDescription: 'IslandsXXXXXXXXXXXXX',
  regularPrice: 70955,
  salePrice: 36230,
  published: false,
  dateCreated: dayjs('2022-10-28T01:15'),
  dateModified: dayjs('2022-10-28'),
  featured: true,
  saleStatus: SaleStatus['CLOSED'],
};

export const sampleWithFullData: IProduct = {
  id: 80395,
  name: 'copying Tuna Marketing',
  slug: 'Multi-lateral',
  description: 'deposit Loaf withdrawal',
  shortDescription: 'Palestinian Universal',
  regularPrice: 35701,
  salePrice: 72826,
  published: true,
  dateCreated: dayjs('2022-10-27T21:08'),
  dateModified: dayjs('2022-10-27'),
  featured: false,
  saleStatus: SaleStatus['SOLD'],
  sharableHash: 'compress Franc',
};

export const sampleWithNewData: NewProduct = {
  name: 'pixel Pound copy',
  slug: 'Salad Dollar',
  description: 'RapidXXXXXXXXXXXXXXX',
  shortDescription: 'synergy VillageXXXXX',
  regularPrice: 34091,
  salePrice: 19769,
  published: false,
  dateCreated: dayjs('2022-10-27T21:22'),
  dateModified: dayjs('2022-10-27'),
  featured: false,
  saleStatus: SaleStatus['SOLD'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
