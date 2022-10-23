import dayjs from 'dayjs/esm';

import { CertificationStatus } from 'app/entities/enumerations/certification-status.model';

import { ICertification, NewCertification } from './certification.model';

export const sampleWithRequiredData: ICertification = {
  id: 98308,
  name: 'Clothing',
  authority: 'reciprocal Supervisor transform',
  status: CertificationStatus['PLANNED'],
  createdBy: 52629,
  createdAt: dayjs('2022-10-22T07:55'),
};

export const sampleWithPartialData: ICertification = {
  id: 30107,
  name: 'Assistant',
  slug: 'withdrawal object-oriented',
  authority: 'Ohio withdrawal Mountains',
  status: CertificationStatus['PLANNED'],
  orgId: 48180,
  facitlityId: 91115,
  createdBy: 91482,
  createdAt: dayjs('2022-10-22T05:08'),
};

export const sampleWithFullData: ICertification = {
  id: 61062,
  name: 'Generic Interactions',
  slug: 'circuit override Awesome',
  authority: 'Fish THX',
  status: CertificationStatus['PLANNED'],
  projectId: 72521,
  prodcut: 61297,
  orgId: 79397,
  facitlityId: 56457,
  createdBy: 7904,
  createdAt: dayjs('2022-10-23T02:31'),
};

export const sampleWithNewData: NewCertification = {
  name: 'AGP',
  authority: 'Licensed',
  status: CertificationStatus['INPROGRESS'],
  createdBy: 3972,
  createdAt: dayjs('2022-10-23T03:44'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
