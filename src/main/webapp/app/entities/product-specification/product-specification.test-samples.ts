import { IProductSpecification, NewProductSpecification } from './product-specification.model';

export const sampleWithRequiredData: IProductSpecification = {
  id: 8277,
  title: 'deposit',
  value: 'back-end',
};

export const sampleWithPartialData: IProductSpecification = {
  id: 50102,
  title: 'Rustic',
  value: 'Fords',
};

export const sampleWithFullData: IProductSpecification = {
  id: 40969,
  title: 'WalkX',
  value: 'Garden',
  description: 'interface opticalXXX',
};

export const sampleWithNewData: NewProductSpecification = {
  title: 'envisioneer Automotive',
  value: 'Berkshire Fresh Nauru',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
