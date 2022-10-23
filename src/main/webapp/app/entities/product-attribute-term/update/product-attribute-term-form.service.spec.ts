import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../product-attribute-term.test-samples';

import { ProductAttributeTermFormService } from './product-attribute-term-form.service';

describe('ProductAttributeTerm Form Service', () => {
  let service: ProductAttributeTermFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductAttributeTermFormService);
  });

  describe('Service methods', () => {
    describe('createProductAttributeTermFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductAttributeTermFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            slug: expect.any(Object),
            description: expect.any(Object),
            menuOrder: expect.any(Object),
            productAttribute: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });

      it('passing IProductAttributeTerm should create a new form with FormGroup', () => {
        const formGroup = service.createProductAttributeTermFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            slug: expect.any(Object),
            description: expect.any(Object),
            menuOrder: expect.any(Object),
            productAttribute: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });
    });

    describe('getProductAttributeTerm', () => {
      it('should return NewProductAttributeTerm for default ProductAttributeTerm initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductAttributeTermFormGroup(sampleWithNewData);

        const productAttributeTerm = service.getProductAttributeTerm(formGroup) as any;

        expect(productAttributeTerm).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductAttributeTerm for empty ProductAttributeTerm initial value', () => {
        const formGroup = service.createProductAttributeTermFormGroup();

        const productAttributeTerm = service.getProductAttributeTerm(formGroup) as any;

        expect(productAttributeTerm).toMatchObject({});
      });

      it('should return IProductAttributeTerm', () => {
        const formGroup = service.createProductAttributeTermFormGroup(sampleWithRequiredData);

        const productAttributeTerm = service.getProductAttributeTerm(formGroup) as any;

        expect(productAttributeTerm).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductAttributeTerm should not enable id FormControl', () => {
        const formGroup = service.createProductAttributeTermFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductAttributeTerm should disable id FormControl', () => {
        const formGroup = service.createProductAttributeTermFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
