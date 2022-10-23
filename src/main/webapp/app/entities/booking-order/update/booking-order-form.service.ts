import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBookingOrder, NewBookingOrder } from '../booking-order.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookingOrder for edit and NewBookingOrderFormGroupInput for create.
 */
type BookingOrderFormGroupInput = IBookingOrder | PartialWithRequiredKeyOf<NewBookingOrder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBookingOrder | NewBookingOrder> = Omit<T, 'placedDate' | 'bookingExpieryDate'> & {
  placedDate?: string | null;
  bookingExpieryDate?: string | null;
};

type BookingOrderFormRawValue = FormValueOf<IBookingOrder>;

type NewBookingOrderFormRawValue = FormValueOf<NewBookingOrder>;

type BookingOrderFormDefaults = Pick<NewBookingOrder, 'id' | 'placedDate' | 'bookingExpieryDate'>;

type BookingOrderFormGroupContent = {
  id: FormControl<BookingOrderFormRawValue['id'] | NewBookingOrder['id']>;
  placedDate: FormControl<BookingOrderFormRawValue['placedDate']>;
  status: FormControl<BookingOrderFormRawValue['status']>;
  code: FormControl<BookingOrderFormRawValue['code']>;
  bookingExpieryDate: FormControl<BookingOrderFormRawValue['bookingExpieryDate']>;
  customer: FormControl<BookingOrderFormRawValue['customer']>;
  productVariation: FormControl<BookingOrderFormRawValue['productVariation']>;
};

export type BookingOrderFormGroup = FormGroup<BookingOrderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookingOrderFormService {
  createBookingOrderFormGroup(bookingOrder: BookingOrderFormGroupInput = { id: null }): BookingOrderFormGroup {
    const bookingOrderRawValue = this.convertBookingOrderToBookingOrderRawValue({
      ...this.getFormDefaults(),
      ...bookingOrder,
    });
    return new FormGroup<BookingOrderFormGroupContent>({
      id: new FormControl(
        { value: bookingOrderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      placedDate: new FormControl(bookingOrderRawValue.placedDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(bookingOrderRawValue.status, {
        validators: [Validators.required],
      }),
      code: new FormControl(bookingOrderRawValue.code, {
        validators: [Validators.minLength(20), Validators.maxLength(250)],
      }),
      bookingExpieryDate: new FormControl(bookingOrderRawValue.bookingExpieryDate),
      customer: new FormControl(bookingOrderRawValue.customer),
      productVariation: new FormControl(bookingOrderRawValue.productVariation, {
        validators: [Validators.required],
      }),
    });
  }

  getBookingOrder(form: BookingOrderFormGroup): IBookingOrder | NewBookingOrder {
    return this.convertBookingOrderRawValueToBookingOrder(form.getRawValue() as BookingOrderFormRawValue | NewBookingOrderFormRawValue);
  }

  resetForm(form: BookingOrderFormGroup, bookingOrder: BookingOrderFormGroupInput): void {
    const bookingOrderRawValue = this.convertBookingOrderToBookingOrderRawValue({ ...this.getFormDefaults(), ...bookingOrder });
    form.reset(
      {
        ...bookingOrderRawValue,
        id: { value: bookingOrderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BookingOrderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      placedDate: currentTime,
      bookingExpieryDate: currentTime,
    };
  }

  private convertBookingOrderRawValueToBookingOrder(
    rawBookingOrder: BookingOrderFormRawValue | NewBookingOrderFormRawValue
  ): IBookingOrder | NewBookingOrder {
    return {
      ...rawBookingOrder,
      placedDate: dayjs(rawBookingOrder.placedDate, DATE_TIME_FORMAT),
      bookingExpieryDate: dayjs(rawBookingOrder.bookingExpieryDate, DATE_TIME_FORMAT),
    };
  }

  private convertBookingOrderToBookingOrderRawValue(
    bookingOrder: IBookingOrder | (Partial<NewBookingOrder> & BookingOrderFormDefaults)
  ): BookingOrderFormRawValue | PartialWithRequiredKeyOf<NewBookingOrderFormRawValue> {
    return {
      ...bookingOrder,
      placedDate: bookingOrder.placedDate ? bookingOrder.placedDate.format(DATE_TIME_FORMAT) : undefined,
      bookingExpieryDate: bookingOrder.bookingExpieryDate ? bookingOrder.bookingExpieryDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
