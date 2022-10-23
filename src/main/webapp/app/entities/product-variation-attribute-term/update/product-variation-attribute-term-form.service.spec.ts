import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../product-variation-attribute-term.test-samples';

import { ProductVariationAttributeTermFormService } from './product-variation-attribute-term-form.service';

describe('ProductVariationAttributeTerm Form Service', () => {
  let service: ProductVariationAttributeTermFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductVariationAttributeTermFormService);
  });

  describe('Service methods', () => {
    describe('createProductVariationAttributeTermFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductVariationAttributeTermFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            slug: expect.any(Object),
            description: expect.any(Object),
            menuOrder: expect.any(Object),
            overRideProductAttribute: expect.any(Object),
            productVariation: expect.any(Object),
          })
        );
      });

      it('passing IProductVariationAttributeTerm should create a new form with FormGroup', () => {
        const formGroup = service.createProductVariationAttributeTermFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            slug: expect.any(Object),
            description: expect.any(Object),
            menuOrder: expect.any(Object),
            overRideProductAttribute: expect.any(Object),
            productVariation: expect.any(Object),
          })
        );
      });
    });

    describe('getProductVariationAttributeTerm', () => {
      it('should return NewProductVariationAttributeTerm for default ProductVariationAttributeTerm initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductVariationAttributeTermFormGroup(sampleWithNewData);

        const productVariationAttributeTerm = service.getProductVariationAttributeTerm(formGroup) as any;

        expect(productVariationAttributeTerm).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductVariationAttributeTerm for empty ProductVariationAttributeTerm initial value', () => {
        const formGroup = service.createProductVariationAttributeTermFormGroup();

        const productVariationAttributeTerm = service.getProductVariationAttributeTerm(formGroup) as any;

        expect(productVariationAttributeTerm).toMatchObject({});
      });

      it('should return IProductVariationAttributeTerm', () => {
        const formGroup = service.createProductVariationAttributeTermFormGroup(sampleWithRequiredData);

        const productVariationAttributeTerm = service.getProductVariationAttributeTerm(formGroup) as any;

        expect(productVariationAttributeTerm).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductVariationAttributeTerm should not enable id FormControl', () => {
        const formGroup = service.createProductVariationAttributeTermFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductVariationAttributeTerm should disable id FormControl', () => {
        const formGroup = service.createProductVariationAttributeTermFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
