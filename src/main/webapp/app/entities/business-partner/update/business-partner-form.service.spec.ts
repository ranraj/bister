import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../business-partner.test-samples';

import { BusinessPartnerFormService } from './business-partner-form.service';

describe('BusinessPartner Form Service', () => {
  let service: BusinessPartnerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BusinessPartnerFormService);
  });

  describe('Service methods', () => {
    describe('createBusinessPartnerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBusinessPartnerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            key: expect.any(Object),
          })
        );
      });

      it('passing IBusinessPartner should create a new form with FormGroup', () => {
        const formGroup = service.createBusinessPartnerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            key: expect.any(Object),
          })
        );
      });
    });

    describe('getBusinessPartner', () => {
      it('should return NewBusinessPartner for default BusinessPartner initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBusinessPartnerFormGroup(sampleWithNewData);

        const businessPartner = service.getBusinessPartner(formGroup) as any;

        expect(businessPartner).toMatchObject(sampleWithNewData);
      });

      it('should return NewBusinessPartner for empty BusinessPartner initial value', () => {
        const formGroup = service.createBusinessPartnerFormGroup();

        const businessPartner = service.getBusinessPartner(formGroup) as any;

        expect(businessPartner).toMatchObject({});
      });

      it('should return IBusinessPartner', () => {
        const formGroup = service.createBusinessPartnerFormGroup(sampleWithRequiredData);

        const businessPartner = service.getBusinessPartner(formGroup) as any;

        expect(businessPartner).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBusinessPartner should not enable id FormControl', () => {
        const formGroup = service.createBusinessPartnerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBusinessPartner should disable id FormControl', () => {
        const formGroup = service.createBusinessPartnerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
