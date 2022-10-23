import { IProductAttributeTerm, NewProductAttributeTerm } from './product-attribute-term.model';

export const sampleWithRequiredData: IProductAttributeTerm = {
  id: 39104,
  name: 'Guadeloupe web-enabled Finland',
  slug: 'invoice',
  description: 'executive',
  menuOrder: 19556,
};

export const sampleWithPartialData: IProductAttributeTerm = {
  id: 74167,
  name: 'calculate UIC-Franc',
  slug: 'blue invoice Practical',
  description: 'Multi-layered algorithm',
  menuOrder: 2311,
};

export const sampleWithFullData: IProductAttributeTerm = {
  id: 6526,
  name: 'whiteboard programming XML',
  slug: 'purple Technician',
  description: 'initiatives',
  menuOrder: 46704,
};

export const sampleWithNewData: NewProductAttributeTerm = {
  name: 'navigate',
  slug: 'Integration Accountability',
  description: 'Sleek',
  menuOrder: 38000,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
