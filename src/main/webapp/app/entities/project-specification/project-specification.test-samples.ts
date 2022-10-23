import { IProjectSpecification, NewProjectSpecification } from './project-specification.model';

export const sampleWithRequiredData: IProjectSpecification = {
  id: 35071,
  title: 'multimedia Buckinghamshire',
  value: 'iterate matrix',
};

export const sampleWithPartialData: IProjectSpecification = {
  id: 85413,
  title: 'WebXX',
  value: 'pixel Solutions',
};

export const sampleWithFullData: IProjectSpecification = {
  id: 3716,
  title: 'Generic',
  value: 'National',
  description: 'indigoXXXXXXXXXXXXXX',
};

export const sampleWithNewData: NewProjectSpecification = {
  title: 'Administrator',
  value: 'users',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
