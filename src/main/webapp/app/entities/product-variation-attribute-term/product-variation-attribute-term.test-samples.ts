import { IProductVariationAttributeTerm, NewProductVariationAttributeTerm } from './product-variation-attribute-term.model';

export const sampleWithRequiredData: IProductVariationAttributeTerm = {
  id: 22699,
  name: 'Cliff payment',
  slug: 'Bike open-source',
  description: 'Legacy Directives Towels',
  menuOrder: 96631,
};

export const sampleWithPartialData: IProductVariationAttributeTerm = {
  id: 21583,
  name: 'Sudanese Profit-focused',
  slug: 'payment Avon',
  description: 'Usability Republic Dobra',
  menuOrder: 24088,
  overRideProductAttribute: true,
};

export const sampleWithFullData: IProductVariationAttributeTerm = {
  id: 31813,
  name: 'optical',
  slug: 'systems Down-sized Metal',
  description: 'Soft Soft',
  menuOrder: 52488,
  overRideProductAttribute: false,
};

export const sampleWithNewData: NewProductVariationAttributeTerm = {
  name: 'Wooden',
  slug: 'tan Villages Account',
  description: 'Bedfordshire Bedfordshire payment',
  menuOrder: 79844,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
