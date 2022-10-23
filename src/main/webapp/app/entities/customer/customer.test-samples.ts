import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 24379,
  name: 'Operations',
  contactNumber: 'Communications ',
};

export const sampleWithPartialData: ICustomer = {
  id: 75018,
  name: 'Namibia Cambridgeshire',
  contactNumber: 'ChipsXXXXX',
  avatarUrl: 'Cotton payment',
};

export const sampleWithFullData: ICustomer = {
  id: 26718,
  name: 'input',
  contactNumber: 'front-end Towel',
  avatarUrl: 'intangible Balanced',
};

export const sampleWithNewData: NewCustomer = {
  name: 'extranet seize',
  contactNumber: 'Facilitator',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
