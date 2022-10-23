import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../attachment.test-samples';

import { AttachmentFormService } from './attachment-form.service';

describe('Attachment Form Service', () => {
  let service: AttachmentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AttachmentFormService);
  });

  describe('Service methods', () => {
    describe('createAttachmentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAttachmentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            attachmentType: expect.any(Object),
            link: expect.any(Object),
            isApprovalNeeded: expect.any(Object),
            approvalStatus: expect.any(Object),
            approvedBy: expect.any(Object),
            attachmentSourceType: expect.any(Object),
            createdBy: expect.any(Object),
            customerId: expect.any(Object),
            agentId: expect.any(Object),
            attachmentVisibilityType: expect.any(Object),
            originalFilename: expect.any(Object),
            extension: expect.any(Object),
            sizeInBytes: expect.any(Object),
            sha256: expect.any(Object),
            contentType: expect.any(Object),
            product: expect.any(Object),
            project: expect.any(Object),
            enquiry: expect.any(Object),
            certification: expect.any(Object),
            productSpecification: expect.any(Object),
            projectSpecification: expect.any(Object),
          })
        );
      });

      it('passing IAttachment should create a new form with FormGroup', () => {
        const formGroup = service.createAttachmentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            attachmentType: expect.any(Object),
            link: expect.any(Object),
            isApprovalNeeded: expect.any(Object),
            approvalStatus: expect.any(Object),
            approvedBy: expect.any(Object),
            attachmentSourceType: expect.any(Object),
            createdBy: expect.any(Object),
            customerId: expect.any(Object),
            agentId: expect.any(Object),
            attachmentVisibilityType: expect.any(Object),
            originalFilename: expect.any(Object),
            extension: expect.any(Object),
            sizeInBytes: expect.any(Object),
            sha256: expect.any(Object),
            contentType: expect.any(Object),
            product: expect.any(Object),
            project: expect.any(Object),
            enquiry: expect.any(Object),
            certification: expect.any(Object),
            productSpecification: expect.any(Object),
            projectSpecification: expect.any(Object),
          })
        );
      });
    });

    describe('getAttachment', () => {
      it('should return NewAttachment for default Attachment initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createAttachmentFormGroup(sampleWithNewData);

        const attachment = service.getAttachment(formGroup) as any;

        expect(attachment).toMatchObject(sampleWithNewData);
      });

      it('should return NewAttachment for empty Attachment initial value', () => {
        const formGroup = service.createAttachmentFormGroup();

        const attachment = service.getAttachment(formGroup) as any;

        expect(attachment).toMatchObject({});
      });

      it('should return IAttachment', () => {
        const formGroup = service.createAttachmentFormGroup(sampleWithRequiredData);

        const attachment = service.getAttachment(formGroup) as any;

        expect(attachment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAttachment should not enable id FormControl', () => {
        const formGroup = service.createAttachmentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAttachment should disable id FormControl', () => {
        const formGroup = service.createAttachmentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
