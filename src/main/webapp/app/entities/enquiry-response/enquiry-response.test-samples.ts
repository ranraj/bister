import { EnquiryResponseType } from 'app/entities/enumerations/enquiry-response-type.model';

import { IEnquiryResponse, NewEnquiryResponse } from './enquiry-response.model';

export const sampleWithRequiredData: IEnquiryResponse = {
  id: 86841,
  enquiryResponseType: EnquiryResponseType['SITE_VISIT'],
};

export const sampleWithPartialData: IEnquiryResponse = {
  id: 98708,
  query: 'bluetooth',
  enquiryResponseType: EnquiryResponseType['SITE_VISIT'],
};

export const sampleWithFullData: IEnquiryResponse = {
  id: 56186,
  query: 'Cheese Florida',
  details: 'orchid',
  enquiryResponseType: EnquiryResponseType['CHAT'],
};

export const sampleWithNewData: NewEnquiryResponse = {
  enquiryResponseType: EnquiryResponseType['CHAT'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
