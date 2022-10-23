import { ActivityStatus } from 'app/entities/enumerations/activity-status.model';

import { IProjectActivity, NewProjectActivity } from './project-activity.model';

export const sampleWithRequiredData: IProjectActivity = {
  id: 96084,
  title: 'blockchains',
  status: ActivityStatus['FAILED'],
};

export const sampleWithPartialData: IProjectActivity = {
  id: 34757,
  title: 'customized enhance Unbranded',
  status: ActivityStatus['HOLD'],
};

export const sampleWithFullData: IProjectActivity = {
  id: 24507,
  title: 'Forint compressing Gloves',
  details: 'adapter Health Solutions',
  status: ActivityStatus['LOG'],
};

export const sampleWithNewData: NewProjectActivity = {
  title: 'online drive monitoring',
  status: ActivityStatus['CANCELLED'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
