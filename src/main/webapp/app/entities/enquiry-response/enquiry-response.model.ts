import { IAgent } from 'app/entities/agent/agent.model';
import { IEnquiry } from 'app/entities/enquiry/enquiry.model';
import { EnquiryResponseType } from 'app/entities/enumerations/enquiry-response-type.model';

export interface IEnquiryResponse {
  id: number;
  query?: string | null;
  details?: string | null;
  enquiryResponseType?: EnquiryResponseType | null;
  agent?: Pick<IAgent, 'id' | 'name'> | null;
  enquiry?: Pick<IEnquiry, 'id'> | null;
}

export type NewEnquiryResponse = Omit<IEnquiryResponse, 'id'> & { id: null };
