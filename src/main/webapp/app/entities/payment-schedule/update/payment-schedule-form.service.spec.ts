import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../payment-schedule.test-samples';

import { PaymentScheduleFormService } from './payment-schedule-form.service';

describe('PaymentSchedule Form Service', () => {
  let service: PaymentScheduleFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentScheduleFormService);
  });

  describe('Service methods', () => {
    describe('createPaymentScheduleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPaymentScheduleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dueDate: expect.any(Object),
            totalPrice: expect.any(Object),
            remarks: expect.any(Object),
            status: expect.any(Object),
            isOverDue: expect.any(Object),
            invoice: expect.any(Object),
            purchaseOrdep: expect.any(Object),
          })
        );
      });

      it('passing IPaymentSchedule should create a new form with FormGroup', () => {
        const formGroup = service.createPaymentScheduleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dueDate: expect.any(Object),
            totalPrice: expect.any(Object),
            remarks: expect.any(Object),
            status: expect.any(Object),
            isOverDue: expect.any(Object),
            invoice: expect.any(Object),
            purchaseOrdep: expect.any(Object),
          })
        );
      });
    });

    describe('getPaymentSchedule', () => {
      it('should return NewPaymentSchedule for default PaymentSchedule initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPaymentScheduleFormGroup(sampleWithNewData);

        const paymentSchedule = service.getPaymentSchedule(formGroup) as any;

        expect(paymentSchedule).toMatchObject(sampleWithNewData);
      });

      it('should return NewPaymentSchedule for empty PaymentSchedule initial value', () => {
        const formGroup = service.createPaymentScheduleFormGroup();

        const paymentSchedule = service.getPaymentSchedule(formGroup) as any;

        expect(paymentSchedule).toMatchObject({});
      });

      it('should return IPaymentSchedule', () => {
        const formGroup = service.createPaymentScheduleFormGroup(sampleWithRequiredData);

        const paymentSchedule = service.getPaymentSchedule(formGroup) as any;

        expect(paymentSchedule).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPaymentSchedule should not enable id FormControl', () => {
        const formGroup = service.createPaymentScheduleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPaymentSchedule should disable id FormControl', () => {
        const formGroup = service.createPaymentScheduleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
