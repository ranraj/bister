import dayjs from 'dayjs/esm';

import { EnquiryType } from 'app/entities/enumerations/enquiry-type.model';
import { EnquiryResolutionStatus } from 'app/entities/enumerations/enquiry-resolution-status.model';

import { IEnquiry, NewEnquiry } from './enquiry.model';

export const sampleWithRequiredData: IEnquiry = {
  id: 45066,
  raisedDate: dayjs('2022-10-22T08:56'),
  subject: 'invoice payment cross-media',
  enquiryType: EnquiryType['SERVICE'],
};

export const sampleWithPartialData: IEnquiry = {
  id: 82196,
  raisedDate: dayjs('2022-10-22T23:19'),
  subject: 'input',
  details: 'Minnesota',
  lastResponseId: 58436,
  enquiryType: EnquiryType['PROJECT'],
};

export const sampleWithFullData: IEnquiry = {
  id: 8887,
  raisedDate: dayjs('2022-10-22T15:53'),
  subject: 'Enterprise-wide disintermediate input',
  details: 'Director',
  lastResponseDate: dayjs('2022-10-22T15:22'),
  lastResponseId: 2761,
  enquiryType: EnquiryType['PROJECT'],
  status: EnquiryResolutionStatus['SPAM'],
};

export const sampleWithNewData: NewEnquiry = {
  raisedDate: dayjs('2022-10-22T07:01'),
  subject: 'best-of-breed eco-centric Fresh',
  enquiryType: EnquiryType['PROJECT'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
