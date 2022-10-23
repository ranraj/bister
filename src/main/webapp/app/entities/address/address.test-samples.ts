import { AddressType } from 'app/entities/enumerations/address-type.model';

import { IAddress, NewAddress } from './address.model';

export const sampleWithRequiredData: IAddress = {
  id: 88754,
  name: 'Bulgarian application Avon',
  addressLine1: 'Court parsing bleeding-edge',
  postcode: 'Rubber neural',
  addressType: AddressType['PERMANENT_RESIDENT'],
};

export const sampleWithPartialData: IAddress = {
  id: 2634,
  name: 'neural bottom-line black',
  addressLine1: 'District',
  state: 'Chair viral Soap',
  postcode: 'bypass United hack',
  addressType: AddressType['PROJECT_SITE'],
};

export const sampleWithFullData: IAddress = {
  id: 35347,
  name: 'quantifying ItalyXXX',
  addressLine1: 'Re-contextualized open-source override',
  addressLine2: 'maximize withdrawal',
  landmark: 'Plaza haptic Hill',
  city: 'Port Pink',
  state: 'THX Identity',
  country: 'Antarctica (the territory South of 60 deg S)',
  postcode: 'infrastructure',
  latitude: 'EXE',
  longitude: 'olive SDD Estate',
  addressType: AddressType['PROJECT_SITE'],
};

export const sampleWithNewData: NewAddress = {
  name: 'ShoesXXXXXXXXXXXXXXX',
  addressLine1: 'National Practical CSS',
  postcode: 'Health tangible Hand',
  addressType: AddressType['PERMANENT_RESIDENT'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
