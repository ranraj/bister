import { IProductAttribute, NewProductAttribute } from './product-attribute.model';

export const sampleWithRequiredData: IProductAttribute = {
  id: 66308,
  name: 'Keyboard Wooden British',
  slug: 'Trafficway Factors',
  type: 'innovative Shilling Central',
  notes: 'Engineer rich Planner',
};

export const sampleWithPartialData: IProductAttribute = {
  id: 95424,
  name: 'deposit iterate',
  slug: 'Alabama',
  type: 'Account supply-chains',
  notes: 'Nevada',
  visible: true,
};

export const sampleWithFullData: IProductAttribute = {
  id: 60849,
  name: 'navigating Credit',
  slug: 'Granite Village',
  type: 'Roads',
  notes: 'Tasty tangible Taiwan',
  visible: false,
};

export const sampleWithNewData: NewProductAttribute = {
  name: 'Aruba',
  slug: 'Square Administrator cross-media',
  type: 'analyzer back-end',
  notes: 'Borders multi-byte',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
