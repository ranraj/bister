import dayjs from 'dayjs/esm';

import { ProjectStatus } from 'app/entities/enumerations/project-status.model';

import { IProject, NewProject } from './project.model';

export const sampleWithRequiredData: IProject = {
  id: 55962,
  name: 'COM dedicated Agent',
  slug: 'Awesome users',
  description: 'Communications Implemented dot-com',
  regularPrice: 50253,
  salePrice: 2265,
  published: false,
  dateCreated: dayjs('2022-10-23T02:25'),
  dateModified: dayjs('2022-10-22'),
  projectStatus: ProjectStatus['SOLD'],
  estimatedBudget: 27765,
};

export const sampleWithPartialData: IProject = {
  id: 60892,
  name: 'Loan Avon',
  slug: 'secondary users invoice',
  description: 'strategize salmon olive',
  shortDescription: 'applications Enterprise-wide Sausages',
  regularPrice: 95553,
  salePrice: 80362,
  published: true,
  dateCreated: dayjs('2022-10-22T14:55'),
  dateModified: dayjs('2022-10-22'),
  projectStatus: ProjectStatus['COMPLETED'],
  sharableHash: 'capability bleeding-edge Cotton',
  estimatedBudget: 47436,
};

export const sampleWithFullData: IProject = {
  id: 69885,
  name: 'infomediaries',
  slug: 'Uzbekistan EXE',
  description: 'leading DeveloperXXX',
  shortDescription: 'open-source AutoXXXX',
  regularPrice: 39852,
  salePrice: 18314,
  published: true,
  dateCreated: dayjs('2022-10-23T03:26'),
  dateModified: dayjs('2022-10-22'),
  projectStatus: ProjectStatus['UNDER_CONTRUCTION'],
  sharableHash: 'Cambridgeshire',
  estimatedBudget: 61275,
};

export const sampleWithNewData: NewProject = {
  name: 'Savings lime Unbranded',
  slug: 'Causeway Wooden',
  description: 'International Cheese azure',
  regularPrice: 60618,
  salePrice: 12555,
  published: false,
  dateCreated: dayjs('2022-10-22T05:23'),
  dateModified: dayjs('2022-10-22'),
  projectStatus: ProjectStatus['PENDING_APPROVAL'],
  estimatedBudget: 16231,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
