import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tax-class.test-samples';

import { TaxClassFormService } from './tax-class-form.service';

describe('TaxClass Form Service', () => {
  let service: TaxClassFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaxClassFormService);
  });

  describe('Service methods', () => {
    describe('createTaxClassFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTaxClassFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            slug: expect.any(Object),
          })
        );
      });

      it('passing ITaxClass should create a new form with FormGroup', () => {
        const formGroup = service.createTaxClassFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            slug: expect.any(Object),
          })
        );
      });
    });

    describe('getTaxClass', () => {
      it('should return NewTaxClass for default TaxClass initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTaxClassFormGroup(sampleWithNewData);

        const taxClass = service.getTaxClass(formGroup) as any;

        expect(taxClass).toMatchObject(sampleWithNewData);
      });

      it('should return NewTaxClass for empty TaxClass initial value', () => {
        const formGroup = service.createTaxClassFormGroup();

        const taxClass = service.getTaxClass(formGroup) as any;

        expect(taxClass).toMatchObject({});
      });

      it('should return ITaxClass', () => {
        const formGroup = service.createTaxClassFormGroup(sampleWithRequiredData);

        const taxClass = service.getTaxClass(formGroup) as any;

        expect(taxClass).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITaxClass should not enable id FormControl', () => {
        const formGroup = service.createTaxClassFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTaxClass should disable id FormControl', () => {
        const formGroup = service.createTaxClassFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
