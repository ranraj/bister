import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../product-variation.test-samples';

import { ProductVariationFormService } from './product-variation-form.service';

describe('ProductVariation Form Service', () => {
  let service: ProductVariationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductVariationFormService);
  });

  describe('Service methods', () => {
    describe('createProductVariationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductVariationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            assetId: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            regularPrice: expect.any(Object),
            salePrice: expect.any(Object),
            dateOnSaleFrom: expect.any(Object),
            dateOnSaleTo: expect.any(Object),
            isDraft: expect.any(Object),
            useParentDetails: expect.any(Object),
            saleStatus: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });

      it('passing IProductVariation should create a new form with FormGroup', () => {
        const formGroup = service.createProductVariationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            assetId: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            regularPrice: expect.any(Object),
            salePrice: expect.any(Object),
            dateOnSaleFrom: expect.any(Object),
            dateOnSaleTo: expect.any(Object),
            isDraft: expect.any(Object),
            useParentDetails: expect.any(Object),
            saleStatus: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });
    });

    describe('getProductVariation', () => {
      it('should return NewProductVariation for default ProductVariation initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductVariationFormGroup(sampleWithNewData);

        const productVariation = service.getProductVariation(formGroup) as any;

        expect(productVariation).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductVariation for empty ProductVariation initial value', () => {
        const formGroup = service.createProductVariationFormGroup();

        const productVariation = service.getProductVariation(formGroup) as any;

        expect(productVariation).toMatchObject({});
      });

      it('should return IProductVariation', () => {
        const formGroup = service.createProductVariationFormGroup(sampleWithRequiredData);

        const productVariation = service.getProductVariation(formGroup) as any;

        expect(productVariation).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductVariation should not enable id FormControl', () => {
        const formGroup = service.createProductVariationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductVariation should disable id FormControl', () => {
        const formGroup = service.createProductVariationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
