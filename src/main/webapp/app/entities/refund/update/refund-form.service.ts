import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRefund, NewRefund } from '../refund.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRefund for edit and NewRefundFormGroupInput for create.
 */
type RefundFormGroupInput = IRefund | PartialWithRequiredKeyOf<NewRefund>;

type RefundFormDefaults = Pick<NewRefund, 'id'>;

type RefundFormGroupContent = {
  id: FormControl<IRefund['id'] | NewRefund['id']>;
  amount: FormControl<IRefund['amount']>;
  reason: FormControl<IRefund['reason']>;
  orderCode: FormControl<IRefund['orderCode']>;
  status: FormControl<IRefund['status']>;
  transaction: FormControl<IRefund['transaction']>;
  user: FormControl<IRefund['user']>;
};

export type RefundFormGroup = FormGroup<RefundFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RefundFormService {
  createRefundFormGroup(refund: RefundFormGroupInput = { id: null }): RefundFormGroup {
    const refundRawValue = {
      ...this.getFormDefaults(),
      ...refund,
    };
    return new FormGroup<RefundFormGroupContent>({
      id: new FormControl(
        { value: refundRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      amount: new FormControl(refundRawValue.amount, {
        validators: [Validators.required],
      }),
      reason: new FormControl(refundRawValue.reason, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(1000)],
      }),
      orderCode: new FormControl(refundRawValue.orderCode, {
        validators: [Validators.required],
      }),
      status: new FormControl(refundRawValue.status, {
        validators: [Validators.required],
      }),
      transaction: new FormControl(refundRawValue.transaction, {
        validators: [Validators.required],
      }),
      user: new FormControl(refundRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getRefund(form: RefundFormGroup): IRefund | NewRefund {
    return form.getRawValue() as IRefund | NewRefund;
  }

  resetForm(form: RefundFormGroup, refund: RefundFormGroupInput): void {
    const refundRawValue = { ...this.getFormDefaults(), ...refund };
    form.reset(
      {
        ...refundRawValue,
        id: { value: refundRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RefundFormDefaults {
    return {
      id: null,
    };
  }
}
