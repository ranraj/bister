import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../enquiry-response.test-samples';

import { EnquiryResponseFormService } from './enquiry-response-form.service';

describe('EnquiryResponse Form Service', () => {
  let service: EnquiryResponseFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnquiryResponseFormService);
  });

  describe('Service methods', () => {
    describe('createEnquiryResponseFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEnquiryResponseFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            query: expect.any(Object),
            details: expect.any(Object),
            enquiryResponseType: expect.any(Object),
            agent: expect.any(Object),
            enquiry: expect.any(Object),
          })
        );
      });

      it('passing IEnquiryResponse should create a new form with FormGroup', () => {
        const formGroup = service.createEnquiryResponseFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            query: expect.any(Object),
            details: expect.any(Object),
            enquiryResponseType: expect.any(Object),
            agent: expect.any(Object),
            enquiry: expect.any(Object),
          })
        );
      });
    });

    describe('getEnquiryResponse', () => {
      it('should return NewEnquiryResponse for default EnquiryResponse initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEnquiryResponseFormGroup(sampleWithNewData);

        const enquiryResponse = service.getEnquiryResponse(formGroup) as any;

        expect(enquiryResponse).toMatchObject(sampleWithNewData);
      });

      it('should return NewEnquiryResponse for empty EnquiryResponse initial value', () => {
        const formGroup = service.createEnquiryResponseFormGroup();

        const enquiryResponse = service.getEnquiryResponse(formGroup) as any;

        expect(enquiryResponse).toMatchObject({});
      });

      it('should return IEnquiryResponse', () => {
        const formGroup = service.createEnquiryResponseFormGroup(sampleWithRequiredData);

        const enquiryResponse = service.getEnquiryResponse(formGroup) as any;

        expect(enquiryResponse).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEnquiryResponse should not enable id FormControl', () => {
        const formGroup = service.createEnquiryResponseFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEnquiryResponse should disable id FormControl', () => {
        const formGroup = service.createEnquiryResponseFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
