import { AttachmentType } from 'app/entities/enumerations/attachment-type.model';
import { AttachmentApprovalStatus } from 'app/entities/enumerations/attachment-approval-status.model';
import { AttachmentSourceType } from 'app/entities/enumerations/attachment-source-type.model';
import { AttachmentVisibilityType } from 'app/entities/enumerations/attachment-visibility-type.model';

import { IAttachment, NewAttachment } from './attachment.model';

export const sampleWithRequiredData: IAttachment = {
  id: 43624,
  name: 'programming Account',
};

export const sampleWithPartialData: IAttachment = {
  id: 43065,
  name: 'Meadow Programmable Sausages',
  description: 'CambridgeshireXXXXXX',
  attachmentType: AttachmentType['LINK'],
  link: 'Hat Cambridgeshire',
  approvalStatus: AttachmentApprovalStatus['INPROGRESS'],
  createdBy: 35816,
  attachmentVisibilityType: AttachmentVisibilityType['LIMITED'],
  originalFilename: 'Streamlined Refined',
  sizeInBytes: 78902,
  sha256: 'executive',
  contentType: 'Burgs',
};

export const sampleWithFullData: IAttachment = {
  id: 7335,
  name: 'orange Gorgeous synthesize',
  description: 'transmit HawaiiXXXXX',
  attachmentType: AttachmentType['DOC'],
  link: 'SSL whiteboard',
  isApprovalNeeded: true,
  approvalStatus: AttachmentApprovalStatus['REJECTED'],
  approvedBy: 70162,
  attachmentSourceType: AttachmentSourceType['Product'],
  createdBy: 93098,
  customerId: 92004,
  agentId: 45848,
  attachmentVisibilityType: AttachmentVisibilityType['PRIVATE'],
  originalFilename: 'Avon motivating Avon',
  extension: 'SCSI',
  sizeInBytes: 57578,
  sha256: 'Home Rubber vertical',
  contentType: 'Ohio Dakota',
};

export const sampleWithNewData: NewAttachment = {
  name: 'Guinea Fantastic',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
