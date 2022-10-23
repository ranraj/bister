import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../enquiry.test-samples';

import { EnquiryFormService } from './enquiry-form.service';

describe('Enquiry Form Service', () => {
  let service: EnquiryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnquiryFormService);
  });

  describe('Service methods', () => {
    describe('createEnquiryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEnquiryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            raisedDate: expect.any(Object),
            subject: expect.any(Object),
            details: expect.any(Object),
            lastResponseDate: expect.any(Object),
            lastResponseId: expect.any(Object),
            enquiryType: expect.any(Object),
            status: expect.any(Object),
            agent: expect.any(Object),
            project: expect.any(Object),
            product: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });

      it('passing IEnquiry should create a new form with FormGroup', () => {
        const formGroup = service.createEnquiryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            raisedDate: expect.any(Object),
            subject: expect.any(Object),
            details: expect.any(Object),
            lastResponseDate: expect.any(Object),
            lastResponseId: expect.any(Object),
            enquiryType: expect.any(Object),
            status: expect.any(Object),
            agent: expect.any(Object),
            project: expect.any(Object),
            product: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });
    });

    describe('getEnquiry', () => {
      it('should return NewEnquiry for default Enquiry initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEnquiryFormGroup(sampleWithNewData);

        const enquiry = service.getEnquiry(formGroup) as any;

        expect(enquiry).toMatchObject(sampleWithNewData);
      });

      it('should return NewEnquiry for empty Enquiry initial value', () => {
        const formGroup = service.createEnquiryFormGroup();

        const enquiry = service.getEnquiry(formGroup) as any;

        expect(enquiry).toMatchObject({});
      });

      it('should return IEnquiry', () => {
        const formGroup = service.createEnquiryFormGroup(sampleWithRequiredData);

        const enquiry = service.getEnquiry(formGroup) as any;

        expect(enquiry).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEnquiry should not enable id FormControl', () => {
        const formGroup = service.createEnquiryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEnquiry should disable id FormControl', () => {
        const formGroup = service.createEnquiryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
