import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../promotion.test-samples';

import { PromotionFormService } from './promotion-form.service';

describe('Promotion Form Service', () => {
  let service: PromotionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PromotionFormService);
  });

  describe('Service methods', () => {
    describe('createPromotionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPromotionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            productId: expect.any(Object),
            projectId: expect.any(Object),
            contentType: expect.any(Object),
            recipients: expect.any(Object),
            recipientGroup: expect.any(Object),
            createdBy: expect.any(Object),
            createdAt: expect.any(Object),
            sendAt: expect.any(Object),
            attachmentId: expect.any(Object),
          })
        );
      });

      it('passing IPromotion should create a new form with FormGroup', () => {
        const formGroup = service.createPromotionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            productId: expect.any(Object),
            projectId: expect.any(Object),
            contentType: expect.any(Object),
            recipients: expect.any(Object),
            recipientGroup: expect.any(Object),
            createdBy: expect.any(Object),
            createdAt: expect.any(Object),
            sendAt: expect.any(Object),
            attachmentId: expect.any(Object),
          })
        );
      });
    });

    describe('getPromotion', () => {
      it('should return NewPromotion for default Promotion initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPromotionFormGroup(sampleWithNewData);

        const promotion = service.getPromotion(formGroup) as any;

        expect(promotion).toMatchObject(sampleWithNewData);
      });

      it('should return NewPromotion for empty Promotion initial value', () => {
        const formGroup = service.createPromotionFormGroup();

        const promotion = service.getPromotion(formGroup) as any;

        expect(promotion).toMatchObject({});
      });

      it('should return IPromotion', () => {
        const formGroup = service.createPromotionFormGroup(sampleWithRequiredData);

        const promotion = service.getPromotion(formGroup) as any;

        expect(promotion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPromotion should not enable id FormControl', () => {
        const formGroup = service.createPromotionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPromotion should disable id FormControl', () => {
        const formGroup = service.createPromotionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
