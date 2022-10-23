import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../refund.test-samples';

import { RefundFormService } from './refund-form.service';

describe('Refund Form Service', () => {
  let service: RefundFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RefundFormService);
  });

  describe('Service methods', () => {
    describe('createRefundFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRefundFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            amount: expect.any(Object),
            reason: expect.any(Object),
            orderCode: expect.any(Object),
            status: expect.any(Object),
            transaction: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IRefund should create a new form with FormGroup', () => {
        const formGroup = service.createRefundFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            amount: expect.any(Object),
            reason: expect.any(Object),
            orderCode: expect.any(Object),
            status: expect.any(Object),
            transaction: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getRefund', () => {
      it('should return NewRefund for default Refund initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRefundFormGroup(sampleWithNewData);

        const refund = service.getRefund(formGroup) as any;

        expect(refund).toMatchObject(sampleWithNewData);
      });

      it('should return NewRefund for empty Refund initial value', () => {
        const formGroup = service.createRefundFormGroup();

        const refund = service.getRefund(formGroup) as any;

        expect(refund).toMatchObject({});
      });

      it('should return IRefund', () => {
        const formGroup = service.createRefundFormGroup(sampleWithRequiredData);

        const refund = service.getRefund(formGroup) as any;

        expect(refund).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRefund should not enable id FormControl', () => {
        const formGroup = service.createRefundFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRefund should disable id FormControl', () => {
        const formGroup = service.createRefundFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
