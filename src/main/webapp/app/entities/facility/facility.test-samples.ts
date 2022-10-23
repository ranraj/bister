import { FacilityType } from 'app/entities/enumerations/facility-type.model';

import { IFacility, NewFacility } from './facility.model';

export const sampleWithRequiredData: IFacility = {
  id: 86127,
  name: 'Coordinator Balanced',
  description: 'Maldives interfaces',
  facilityType: FacilityType['CONSULTING'],
};

export const sampleWithPartialData: IFacility = {
  id: 3203,
  name: 'XSS bypassing Handmade',
  description: 'dot-com Architect Ergonomic',
  facilityType: FacilityType['DEMO'],
};

export const sampleWithFullData: IFacility = {
  id: 4228,
  name: 'Colorado microchip data-warehouse',
  description: 'Motorway indigo',
  facilityType: FacilityType['STALL'],
};

export const sampleWithNewData: NewFacility = {
  name: 'zero Lead',
  description: 'ubiquitous Chair Cambridgeshire',
  facilityType: FacilityType['PROMOTION'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
