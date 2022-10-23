import { PhonenumberType } from 'app/entities/enumerations/phonenumber-type.model';

import { IPhonenumber, NewPhonenumber } from './phonenumber.model';

export const sampleWithRequiredData: IPhonenumber = {
  id: 77985,
  code: 'Buckingham',
  contactNumber: 'Bedfordshire mo',
  phonenumberType: PhonenumberType['OFFICE_SECONDARY'],
};

export const sampleWithPartialData: IPhonenumber = {
  id: 6260,
  country: 'Liechtenstein',
  code: 'asynchrono',
  contactNumber: 'seamless magnet',
  phonenumberType: PhonenumberType['OFFICIAL_NUMBER1'],
};

export const sampleWithFullData: IPhonenumber = {
  id: 16959,
  country: 'Tajikistan',
  code: 'Up-sized U',
  contactNumber: 'Kids Berkshire',
  phonenumberType: PhonenumberType['OFFICIAL_NUMBER1'],
};

export const sampleWithNewData: NewPhonenumber = {
  code: 'one-to-one',
  contactNumber: 'Bedfordshire',
  phonenumberType: PhonenumberType['OFFICIAL_NUMBER2'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
