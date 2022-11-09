import { IProduct } from 'app/entities/product/product.model';
import { IProject } from 'app/entities/project/project.model';
import { IEnquiry } from 'app/enquiry/enquiry.model';
import { ICertification } from 'app/entities/certification/certification.model';
import { IProductSpecification } from 'app/entities/product-specification/product-specification.model';
import { IProjectSpecification } from 'app/entities/project-specification/project-specification.model';
import { AttachmentType } from 'app/entities/enumerations/attachment-type.model';
import { AttachmentApprovalStatus } from 'app/entities/enumerations/attachment-approval-status.model';
import { AttachmentSourceType } from 'app/entities/enumerations/attachment-source-type.model';
import { AttachmentVisibilityType } from 'app/entities/enumerations/attachment-visibility-type.model';

export interface IAttachment {
  id: number;
  name?: string | null;
  description?: string | null;
  attachmentType?: AttachmentType | null;
  link?: string | null;
  isApprovalNeeded?: boolean | null;
  approvalStatus?: AttachmentApprovalStatus | null;
  approvedBy?: number | null;
  attachmentSourceType?: AttachmentSourceType | null;
  createdBy?: number | null;
  customerId?: number | null;
  agentId?: number | null;
  attachmentVisibilityType?: AttachmentVisibilityType | null;
  originalFilename?: string | null;
  extension?: string | null;
  sizeInBytes?: number | null;
  sha256?: string | null;
  contentType?: string | null;
  product?: Pick<IProduct, 'id' | 'name'> | null;
  project?: Pick<IProject, 'id' | 'name'> | null;
  enquiry?: Pick<IEnquiry, 'id' | 'subject'> | null;
  certification?: Pick<ICertification, 'id' | 'name'> | null;
  productSpecification?: Pick<IProductSpecification, 'id' | 'title'> | null;
  projectSpecification?: Pick<IProjectSpecification, 'id' | 'title'> | null;
}

export type NewAttachment = Omit<IAttachment, 'id'> & { id: null };
