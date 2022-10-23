import { ActivityStatus } from 'app/entities/enumerations/activity-status.model';

import { IProductActivity, NewProductActivity } from './product-activity.model';

export const sampleWithRequiredData: IProductActivity = {
  id: 39816,
  title: 'invoice',
  status: ActivityStatus['UPDATE'],
};

export const sampleWithPartialData: IProductActivity = {
  id: 24186,
  title: 'Manager Supervisor',
  status: ActivityStatus['LOG'],
};

export const sampleWithFullData: IProductActivity = {
  id: 52771,
  title: 'Dinar',
  details: 'quantifyingXXXXXXXXX',
  status: ActivityStatus['NEW'],
};

export const sampleWithNewData: NewProductActivity = {
  title: 'Checking cultivate Rubber',
  status: ActivityStatus['LOG'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
