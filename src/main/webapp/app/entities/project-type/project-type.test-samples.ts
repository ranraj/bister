import { IProjectType, NewProjectType } from './project-type.model';

export const sampleWithRequiredData: IProjectType = {
  id: 60796,
  name: 'Lock Granite methodical',
};

export const sampleWithPartialData: IProjectType = {
  id: 41015,
  name: 'Sleek reboot',
};

export const sampleWithFullData: IProjectType = {
  id: 45353,
  name: 'De-engineered',
  description: 'Loan customized Avon',
};

export const sampleWithNewData: NewProjectType = {
  name: 'Soft',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
