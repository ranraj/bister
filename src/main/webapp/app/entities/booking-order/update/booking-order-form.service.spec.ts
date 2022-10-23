import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../booking-order.test-samples';

import { BookingOrderFormService } from './booking-order-form.service';

describe('BookingOrder Form Service', () => {
  let service: BookingOrderFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookingOrderFormService);
  });

  describe('Service methods', () => {
    describe('createBookingOrderFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBookingOrderFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            placedDate: expect.any(Object),
            status: expect.any(Object),
            code: expect.any(Object),
            bookingExpieryDate: expect.any(Object),
            customer: expect.any(Object),
            productVariation: expect.any(Object),
          })
        );
      });

      it('passing IBookingOrder should create a new form with FormGroup', () => {
        const formGroup = service.createBookingOrderFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            placedDate: expect.any(Object),
            status: expect.any(Object),
            code: expect.any(Object),
            bookingExpieryDate: expect.any(Object),
            customer: expect.any(Object),
            productVariation: expect.any(Object),
          })
        );
      });
    });

    describe('getBookingOrder', () => {
      it('should return NewBookingOrder for default BookingOrder initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBookingOrderFormGroup(sampleWithNewData);

        const bookingOrder = service.getBookingOrder(formGroup) as any;

        expect(bookingOrder).toMatchObject(sampleWithNewData);
      });

      it('should return NewBookingOrder for empty BookingOrder initial value', () => {
        const formGroup = service.createBookingOrderFormGroup();

        const bookingOrder = service.getBookingOrder(formGroup) as any;

        expect(bookingOrder).toMatchObject({});
      });

      it('should return IBookingOrder', () => {
        const formGroup = service.createBookingOrderFormGroup(sampleWithRequiredData);

        const bookingOrder = service.getBookingOrder(formGroup) as any;

        expect(bookingOrder).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBookingOrder should not enable id FormControl', () => {
        const formGroup = service.createBookingOrderFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBookingOrder should disable id FormControl', () => {
        const formGroup = service.createBookingOrderFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
