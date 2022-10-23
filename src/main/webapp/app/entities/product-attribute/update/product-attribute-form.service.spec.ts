import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../product-attribute.test-samples';

import { ProductAttributeFormService } from './product-attribute-form.service';

describe('ProductAttribute Form Service', () => {
  let service: ProductAttributeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductAttributeFormService);
  });

  describe('Service methods', () => {
    describe('createProductAttributeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductAttributeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            slug: expect.any(Object),
            type: expect.any(Object),
            notes: expect.any(Object),
            visible: expect.any(Object),
          })
        );
      });

      it('passing IProductAttribute should create a new form with FormGroup', () => {
        const formGroup = service.createProductAttributeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            slug: expect.any(Object),
            type: expect.any(Object),
            notes: expect.any(Object),
            visible: expect.any(Object),
          })
        );
      });
    });

    describe('getProductAttribute', () => {
      it('should return NewProductAttribute for default ProductAttribute initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductAttributeFormGroup(sampleWithNewData);

        const productAttribute = service.getProductAttribute(formGroup) as any;

        expect(productAttribute).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductAttribute for empty ProductAttribute initial value', () => {
        const formGroup = service.createProductAttributeFormGroup();

        const productAttribute = service.getProductAttribute(formGroup) as any;

        expect(productAttribute).toMatchObject({});
      });

      it('should return IProductAttribute', () => {
        const formGroup = service.createProductAttributeFormGroup(sampleWithRequiredData);

        const productAttribute = service.getProductAttribute(formGroup) as any;

        expect(productAttribute).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductAttribute should not enable id FormControl', () => {
        const formGroup = service.createProductAttributeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductAttribute should disable id FormControl', () => {
        const formGroup = service.createProductAttributeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
