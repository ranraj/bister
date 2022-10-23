import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../product-review.test-samples';

import { ProductReviewFormService } from './product-review-form.service';

describe('ProductReview Form Service', () => {
  let service: ProductReviewFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductReviewFormService);
  });

  describe('Service methods', () => {
    describe('createProductReviewFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductReviewFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reviewerName: expect.any(Object),
            reviewerEmail: expect.any(Object),
            review: expect.any(Object),
            rating: expect.any(Object),
            status: expect.any(Object),
            reviewerId: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });

      it('passing IProductReview should create a new form with FormGroup', () => {
        const formGroup = service.createProductReviewFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reviewerName: expect.any(Object),
            reviewerEmail: expect.any(Object),
            review: expect.any(Object),
            rating: expect.any(Object),
            status: expect.any(Object),
            reviewerId: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });
    });

    describe('getProductReview', () => {
      it('should return NewProductReview for default ProductReview initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductReviewFormGroup(sampleWithNewData);

        const productReview = service.getProductReview(formGroup) as any;

        expect(productReview).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductReview for empty ProductReview initial value', () => {
        const formGroup = service.createProductReviewFormGroup();

        const productReview = service.getProductReview(formGroup) as any;

        expect(productReview).toMatchObject({});
      });

      it('should return IProductReview', () => {
        const formGroup = service.createProductReviewFormGroup(sampleWithRequiredData);

        const productReview = service.getProductReview(formGroup) as any;

        expect(productReview).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductReview should not enable id FormControl', () => {
        const formGroup = service.createProductReviewFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductReview should disable id FormControl', () => {
        const formGroup = service.createProductReviewFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
