import dayjs from 'dayjs/esm';
import { CertificationStatus } from 'app/entities/enumerations/certification-status.model';

export interface ICertification {
  id: number;
  name?: string | null;
  slug?: string | null;
  authority?: string | null;
  status?: CertificationStatus | null;
  projectId?: number | null;
  prodcut?: number | null;
  orgId?: number | null;
  facitlityId?: number | null;
  createdBy?: number | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewCertification = Omit<ICertification, 'id'> & { id: null };
