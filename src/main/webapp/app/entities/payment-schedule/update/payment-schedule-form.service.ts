import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPaymentSchedule, NewPaymentSchedule } from '../payment-schedule.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPaymentSchedule for edit and NewPaymentScheduleFormGroupInput for create.
 */
type PaymentScheduleFormGroupInput = IPaymentSchedule | PartialWithRequiredKeyOf<NewPaymentSchedule>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPaymentSchedule | NewPaymentSchedule> = Omit<T, 'dueDate'> & {
  dueDate?: string | null;
};

type PaymentScheduleFormRawValue = FormValueOf<IPaymentSchedule>;

type NewPaymentScheduleFormRawValue = FormValueOf<NewPaymentSchedule>;

type PaymentScheduleFormDefaults = Pick<NewPaymentSchedule, 'id' | 'dueDate' | 'isOverDue'>;

type PaymentScheduleFormGroupContent = {
  id: FormControl<PaymentScheduleFormRawValue['id'] | NewPaymentSchedule['id']>;
  dueDate: FormControl<PaymentScheduleFormRawValue['dueDate']>;
  totalPrice: FormControl<PaymentScheduleFormRawValue['totalPrice']>;
  remarks: FormControl<PaymentScheduleFormRawValue['remarks']>;
  status: FormControl<PaymentScheduleFormRawValue['status']>;
  isOverDue: FormControl<PaymentScheduleFormRawValue['isOverDue']>;
  invoice: FormControl<PaymentScheduleFormRawValue['invoice']>;
  purchaseOrdep: FormControl<PaymentScheduleFormRawValue['purchaseOrdep']>;
};

export type PaymentScheduleFormGroup = FormGroup<PaymentScheduleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaymentScheduleFormService {
  createPaymentScheduleFormGroup(paymentSchedule: PaymentScheduleFormGroupInput = { id: null }): PaymentScheduleFormGroup {
    const paymentScheduleRawValue = this.convertPaymentScheduleToPaymentScheduleRawValue({
      ...this.getFormDefaults(),
      ...paymentSchedule,
    });
    return new FormGroup<PaymentScheduleFormGroupContent>({
      id: new FormControl(
        { value: paymentScheduleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      dueDate: new FormControl(paymentScheduleRawValue.dueDate, {
        validators: [Validators.required],
      }),
      totalPrice: new FormControl(paymentScheduleRawValue.totalPrice, {
        validators: [Validators.required, Validators.min(0)],
      }),
      remarks: new FormControl(paymentScheduleRawValue.remarks, {
        validators: [Validators.minLength(20), Validators.maxLength(250)],
      }),
      status: new FormControl(paymentScheduleRawValue.status, {
        validators: [Validators.required],
      }),
      isOverDue: new FormControl(paymentScheduleRawValue.isOverDue),
      invoice: new FormControl(paymentScheduleRawValue.invoice),
      purchaseOrdep: new FormControl(paymentScheduleRawValue.purchaseOrdep),
    });
  }

  getPaymentSchedule(form: PaymentScheduleFormGroup): IPaymentSchedule | NewPaymentSchedule {
    return this.convertPaymentScheduleRawValueToPaymentSchedule(
      form.getRawValue() as PaymentScheduleFormRawValue | NewPaymentScheduleFormRawValue
    );
  }

  resetForm(form: PaymentScheduleFormGroup, paymentSchedule: PaymentScheduleFormGroupInput): void {
    const paymentScheduleRawValue = this.convertPaymentScheduleToPaymentScheduleRawValue({ ...this.getFormDefaults(), ...paymentSchedule });
    form.reset(
      {
        ...paymentScheduleRawValue,
        id: { value: paymentScheduleRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PaymentScheduleFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dueDate: currentTime,
      isOverDue: false,
    };
  }

  private convertPaymentScheduleRawValueToPaymentSchedule(
    rawPaymentSchedule: PaymentScheduleFormRawValue | NewPaymentScheduleFormRawValue
  ): IPaymentSchedule | NewPaymentSchedule {
    return {
      ...rawPaymentSchedule,
      dueDate: dayjs(rawPaymentSchedule.dueDate, DATE_TIME_FORMAT),
    };
  }

  private convertPaymentScheduleToPaymentScheduleRawValue(
    paymentSchedule: IPaymentSchedule | (Partial<NewPaymentSchedule> & PaymentScheduleFormDefaults)
  ): PaymentScheduleFormRawValue | PartialWithRequiredKeyOf<NewPaymentScheduleFormRawValue> {
    return {
      ...paymentSchedule,
      dueDate: paymentSchedule.dueDate ? paymentSchedule.dueDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
