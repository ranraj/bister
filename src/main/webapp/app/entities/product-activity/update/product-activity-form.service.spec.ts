import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../product-activity.test-samples';

import { ProductActivityFormService } from './product-activity-form.service';

describe('ProductActivity Form Service', () => {
  let service: ProductActivityFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductActivityFormService);
  });

  describe('Service methods', () => {
    describe('createProductActivityFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductActivityFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            details: expect.any(Object),
            status: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });

      it('passing IProductActivity should create a new form with FormGroup', () => {
        const formGroup = service.createProductActivityFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            details: expect.any(Object),
            status: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });
    });

    describe('getProductActivity', () => {
      it('should return NewProductActivity for default ProductActivity initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductActivityFormGroup(sampleWithNewData);

        const productActivity = service.getProductActivity(formGroup) as any;

        expect(productActivity).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductActivity for empty ProductActivity initial value', () => {
        const formGroup = service.createProductActivityFormGroup();

        const productActivity = service.getProductActivity(formGroup) as any;

        expect(productActivity).toMatchObject({});
      });

      it('should return IProductActivity', () => {
        const formGroup = service.createProductActivityFormGroup(sampleWithRequiredData);

        const productActivity = service.getProductActivity(formGroup) as any;

        expect(productActivity).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductActivity should not enable id FormControl', () => {
        const formGroup = service.createProductActivityFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductActivity should disable id FormControl', () => {
        const formGroup = service.createProductActivityFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
