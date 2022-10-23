import { IProjectSpecificationGroup, NewProjectSpecificationGroup } from './project-specification-group.model';

export const sampleWithRequiredData: IProjectSpecificationGroup = {
  id: 44396,
  title: 'logistical',
};

export const sampleWithPartialData: IProjectSpecificationGroup = {
  id: 94880,
  title: 'pixel',
};

export const sampleWithFullData: IProjectSpecificationGroup = {
  id: 59622,
  title: 'optimize Table',
  slug: 'global Mexican',
  description: 'Comoros program',
};

export const sampleWithNewData: NewProjectSpecificationGroup = {
  title: 'North Toys',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
