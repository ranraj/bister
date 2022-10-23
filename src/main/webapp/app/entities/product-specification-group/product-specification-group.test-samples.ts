import { IProductSpecificationGroup, NewProductSpecificationGroup } from './product-specification-group.model';

export const sampleWithRequiredData: IProductSpecificationGroup = {
  id: 82539,
  title: 'Avenue',
  slug: 'Rubber',
};

export const sampleWithPartialData: IProductSpecificationGroup = {
  id: 94104,
  title: 'Health',
  slug: 'Strategist Shoes magenta',
};

export const sampleWithFullData: IProductSpecificationGroup = {
  id: 79329,
  title: 'synthesize Optimization Fish',
  slug: 'Ball microchip Account',
  description: 'Cheese Granite Movies',
};

export const sampleWithNewData: NewProductSpecificationGroup = {
  title: 'Industrial',
  slug: 'Fresh',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
