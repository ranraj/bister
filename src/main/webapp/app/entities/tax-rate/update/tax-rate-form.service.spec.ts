import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tax-rate.test-samples';

import { TaxRateFormService } from './tax-rate-form.service';

describe('TaxRate Form Service', () => {
  let service: TaxRateFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaxRateFormService);
  });

  describe('Service methods', () => {
    describe('createTaxRateFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTaxRateFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            country: expect.any(Object),
            state: expect.any(Object),
            postcode: expect.any(Object),
            city: expect.any(Object),
            rate: expect.any(Object),
            name: expect.any(Object),
            compound: expect.any(Object),
            priority: expect.any(Object),
            taxClass: expect.any(Object),
          })
        );
      });

      it('passing ITaxRate should create a new form with FormGroup', () => {
        const formGroup = service.createTaxRateFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            country: expect.any(Object),
            state: expect.any(Object),
            postcode: expect.any(Object),
            city: expect.any(Object),
            rate: expect.any(Object),
            name: expect.any(Object),
            compound: expect.any(Object),
            priority: expect.any(Object),
            taxClass: expect.any(Object),
          })
        );
      });
    });

    describe('getTaxRate', () => {
      it('should return NewTaxRate for default TaxRate initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTaxRateFormGroup(sampleWithNewData);

        const taxRate = service.getTaxRate(formGroup) as any;

        expect(taxRate).toMatchObject(sampleWithNewData);
      });

      it('should return NewTaxRate for empty TaxRate initial value', () => {
        const formGroup = service.createTaxRateFormGroup();

        const taxRate = service.getTaxRate(formGroup) as any;

        expect(taxRate).toMatchObject({});
      });

      it('should return ITaxRate', () => {
        const formGroup = service.createTaxRateFormGroup(sampleWithRequiredData);

        const taxRate = service.getTaxRate(formGroup) as any;

        expect(taxRate).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITaxRate should not enable id FormControl', () => {
        const formGroup = service.createTaxRateFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTaxRate should disable id FormControl', () => {
        const formGroup = service.createTaxRateFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
