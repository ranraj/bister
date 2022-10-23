import { ITaxClass, NewTaxClass } from './tax-class.model';

export const sampleWithRequiredData: ITaxClass = {
  id: 95993,
  name: 'Concrete driver Lead',
  slug: 'Bangladesh Granite Officer',
};

export const sampleWithPartialData: ITaxClass = {
  id: 53582,
  name: 'generating COM Money',
  slug: 'Slovenia Baby',
};

export const sampleWithFullData: ITaxClass = {
  id: 47922,
  name: 'navigate Grass-roots',
  slug: 'Refined',
};

export const sampleWithNewData: NewTaxClass = {
  name: 'Engineer',
  slug: '6th',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
