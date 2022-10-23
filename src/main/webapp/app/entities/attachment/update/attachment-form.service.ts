import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAttachment, NewAttachment } from '../attachment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAttachment for edit and NewAttachmentFormGroupInput for create.
 */
type AttachmentFormGroupInput = IAttachment | PartialWithRequiredKeyOf<NewAttachment>;

type AttachmentFormDefaults = Pick<NewAttachment, 'id' | 'isApprovalNeeded'>;

type AttachmentFormGroupContent = {
  id: FormControl<IAttachment['id'] | NewAttachment['id']>;
  name: FormControl<IAttachment['name']>;
  description: FormControl<IAttachment['description']>;
  attachmentType: FormControl<IAttachment['attachmentType']>;
  link: FormControl<IAttachment['link']>;
  isApprovalNeeded: FormControl<IAttachment['isApprovalNeeded']>;
  approvalStatus: FormControl<IAttachment['approvalStatus']>;
  approvedBy: FormControl<IAttachment['approvedBy']>;
  attachmentSourceType: FormControl<IAttachment['attachmentSourceType']>;
  createdBy: FormControl<IAttachment['createdBy']>;
  customerId: FormControl<IAttachment['customerId']>;
  agentId: FormControl<IAttachment['agentId']>;
  attachmentVisibilityType: FormControl<IAttachment['attachmentVisibilityType']>;
  originalFilename: FormControl<IAttachment['originalFilename']>;
  extension: FormControl<IAttachment['extension']>;
  sizeInBytes: FormControl<IAttachment['sizeInBytes']>;
  sha256: FormControl<IAttachment['sha256']>;
  contentType: FormControl<IAttachment['contentType']>;
  product: FormControl<IAttachment['product']>;
  project: FormControl<IAttachment['project']>;
  enquiry: FormControl<IAttachment['enquiry']>;
  certification: FormControl<IAttachment['certification']>;
  productSpecification: FormControl<IAttachment['productSpecification']>;
  projectSpecification: FormControl<IAttachment['projectSpecification']>;
};

export type AttachmentFormGroup = FormGroup<AttachmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AttachmentFormService {
  createAttachmentFormGroup(attachment: AttachmentFormGroupInput = { id: null }): AttachmentFormGroup {
    const attachmentRawValue = {
      ...this.getFormDefaults(),
      ...attachment,
    };
    return new FormGroup<AttachmentFormGroupContent>({
      id: new FormControl(
        { value: attachmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(attachmentRawValue.name, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100)],
      }),
      description: new FormControl(attachmentRawValue.description, {
        validators: [Validators.minLength(20), Validators.maxLength(250)],
      }),
      attachmentType: new FormControl(attachmentRawValue.attachmentType),
      link: new FormControl(attachmentRawValue.link, {
        validators: [Validators.minLength(2), Validators.maxLength(250)],
      }),
      isApprovalNeeded: new FormControl(attachmentRawValue.isApprovalNeeded),
      approvalStatus: new FormControl(attachmentRawValue.approvalStatus),
      approvedBy: new FormControl(attachmentRawValue.approvedBy),
      attachmentSourceType: new FormControl(attachmentRawValue.attachmentSourceType),
      createdBy: new FormControl(attachmentRawValue.createdBy),
      customerId: new FormControl(attachmentRawValue.customerId),
      agentId: new FormControl(attachmentRawValue.agentId),
      attachmentVisibilityType: new FormControl(attachmentRawValue.attachmentVisibilityType),
      originalFilename: new FormControl(attachmentRawValue.originalFilename),
      extension: new FormControl(attachmentRawValue.extension),
      sizeInBytes: new FormControl(attachmentRawValue.sizeInBytes),
      sha256: new FormControl(attachmentRawValue.sha256),
      contentType: new FormControl(attachmentRawValue.contentType),
      product: new FormControl(attachmentRawValue.product),
      project: new FormControl(attachmentRawValue.project),
      enquiry: new FormControl(attachmentRawValue.enquiry),
      certification: new FormControl(attachmentRawValue.certification),
      productSpecification: new FormControl(attachmentRawValue.productSpecification),
      projectSpecification: new FormControl(attachmentRawValue.projectSpecification),
    });
  }

  getAttachment(form: AttachmentFormGroup): IAttachment | NewAttachment {
    return form.getRawValue() as IAttachment | NewAttachment;
  }

  resetForm(form: AttachmentFormGroup, attachment: AttachmentFormGroupInput): void {
    const attachmentRawValue = { ...this.getFormDefaults(), ...attachment };
    form.reset(
      {
        ...attachmentRawValue,
        id: { value: attachmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AttachmentFormDefaults {
    return {
      id: null,
      isApprovalNeeded: false,
    };
  }
}
