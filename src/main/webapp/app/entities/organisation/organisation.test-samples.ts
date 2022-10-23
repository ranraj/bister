import { IOrganisation, NewOrganisation } from './organisation.model';

export const sampleWithRequiredData: IOrganisation = {
  id: 2172,
  name: 'Associate Grocery XML',
  description: 'Planner blue Pizza',
  key: 'groupware Cambridgeshire Generic',
};

export const sampleWithPartialData: IOrganisation = {
  id: 68921,
  name: 'engine Burundi',
  description: 'Division',
  key: 'Clothing Kentucky Loan',
};

export const sampleWithFullData: IOrganisation = {
  id: 79435,
  name: 'well-modulated Pizza',
  description: 'microchip',
  key: 'Checking Dalasi',
};

export const sampleWithNewData: NewOrganisation = {
  name: 'Function-based',
  description: 'Planner Dynamic Producer',
  key: 'Soft firewall Games',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
