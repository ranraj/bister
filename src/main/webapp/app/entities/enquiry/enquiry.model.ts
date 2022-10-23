import dayjs from 'dayjs/esm';
import { IAgent } from 'app/entities/agent/agent.model';
import { IProject } from 'app/entities/project/project.model';
import { IProduct } from 'app/entities/product/product.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { EnquiryType } from 'app/entities/enumerations/enquiry-type.model';
import { EnquiryResolutionStatus } from 'app/entities/enumerations/enquiry-resolution-status.model';

export interface IEnquiry {
  id: number;
  raisedDate?: dayjs.Dayjs | null;
  subject?: string | null;
  details?: string | null;
  lastResponseDate?: dayjs.Dayjs | null;
  lastResponseId?: number | null;
  enquiryType?: EnquiryType | null;
  status?: EnquiryResolutionStatus | null;
  agent?: Pick<IAgent, 'id' | 'name'> | null;
  project?: Pick<IProject, 'id' | 'name'> | null;
  product?: Pick<IProduct, 'id' | 'name'> | null;
  customer?: Pick<ICustomer, 'id' | 'name'> | null;
}

export type NewEnquiry = Omit<IEnquiry, 'id'> & { id: null };
