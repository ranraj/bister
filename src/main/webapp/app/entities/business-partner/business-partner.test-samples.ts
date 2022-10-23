import { IBusinessPartner, NewBusinessPartner } from './business-partner.model';

export const sampleWithRequiredData: IBusinessPartner = {
  id: 23681,
  name: 'Robust',
  description: 'Identity',
  key: 'Argentine',
};

export const sampleWithPartialData: IBusinessPartner = {
  id: 76536,
  name: 'Handcrafted Account application',
  description: 'deliver Antilles hybrid',
  key: 'Hills Internal Tuna',
};

export const sampleWithFullData: IBusinessPartner = {
  id: 41177,
  name: 'Account',
  description: 'Massachusetts',
  key: 'product',
};

export const sampleWithNewData: NewBusinessPartner = {
  name: 'synthesizing Tuna',
  description: 'Sharable Freeway',
  key: 'Rubber',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
