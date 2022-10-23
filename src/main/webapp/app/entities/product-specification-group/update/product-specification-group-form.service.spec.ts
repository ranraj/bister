import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../product-specification-group.test-samples';

import { ProductSpecificationGroupFormService } from './product-specification-group-form.service';

describe('ProductSpecificationGroup Form Service', () => {
  let service: ProductSpecificationGroupFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductSpecificationGroupFormService);
  });

  describe('Service methods', () => {
    describe('createProductSpecificationGroupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductSpecificationGroupFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            slug: expect.any(Object),
            description: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });

      it('passing IProductSpecificationGroup should create a new form with FormGroup', () => {
        const formGroup = service.createProductSpecificationGroupFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            slug: expect.any(Object),
            description: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });
    });

    describe('getProductSpecificationGroup', () => {
      it('should return NewProductSpecificationGroup for default ProductSpecificationGroup initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductSpecificationGroupFormGroup(sampleWithNewData);

        const productSpecificationGroup = service.getProductSpecificationGroup(formGroup) as any;

        expect(productSpecificationGroup).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductSpecificationGroup for empty ProductSpecificationGroup initial value', () => {
        const formGroup = service.createProductSpecificationGroupFormGroup();

        const productSpecificationGroup = service.getProductSpecificationGroup(formGroup) as any;

        expect(productSpecificationGroup).toMatchObject({});
      });

      it('should return IProductSpecificationGroup', () => {
        const formGroup = service.createProductSpecificationGroupFormGroup(sampleWithRequiredData);

        const productSpecificationGroup = service.getProductSpecificationGroup(formGroup) as any;

        expect(productSpecificationGroup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductSpecificationGroup should not enable id FormControl', () => {
        const formGroup = service.createProductSpecificationGroupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductSpecificationGroup should disable id FormControl', () => {
        const formGroup = service.createProductSpecificationGroupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
