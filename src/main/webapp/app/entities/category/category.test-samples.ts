import { CategoryType } from 'app/entities/enumerations/category-type.model';

import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 2529,
  name: 'Namibia Sausages',
  slug: 'Clothing navigate gold',
};

export const sampleWithPartialData: ICategory = {
  id: 35731,
  name: 'virtual Chair navigating',
  slug: 'Realigned Handmade',
  description: 'Concrete Steel transform',
  categoryType: CategoryType['Product'],
};

export const sampleWithFullData: ICategory = {
  id: 61077,
  name: 'pixel syndicate',
  slug: 'Group indexing Kong',
  description: 'withdrawal Cotton Steel',
  categoryType: CategoryType['Project'],
};

export const sampleWithNewData: NewCategory = {
  name: 'parse',
  slug: 'compressing Pizza',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
