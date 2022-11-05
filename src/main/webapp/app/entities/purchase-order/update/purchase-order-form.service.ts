import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPurchaseOrder, NewPurchaseOrder } from '../purchase-order.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPurchaseOrder for edit and NewPurchaseOrderFormGroupInput for create.
 */
type PurchaseOrderFormGroupInput = IPurchaseOrder | PartialWithRequiredKeyOf<NewPurchaseOrder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPurchaseOrder | NewPurchaseOrder> = Omit<T, 'placedDate'> & {
  placedDate?: string | null;
};

type PurchaseOrderFormRawValue = FormValueOf<IPurchaseOrder>;

type NewPurchaseOrderFormRawValue = FormValueOf<NewPurchaseOrder>;

type PurchaseOrderFormDefaults = Pick<NewPurchaseOrder, 'id' | 'placedDate'>;

type PurchaseOrderFormGroupContent = {
  id: FormControl<PurchaseOrderFormRawValue['id'] | NewPurchaseOrder['id']>;
  placedDate: FormControl<PurchaseOrderFormRawValue['placedDate']>;
  status: FormControl<PurchaseOrderFormRawValue['status']>;
  code: FormControl<PurchaseOrderFormRawValue['code']>;
  deliveryOption: FormControl<PurchaseOrderFormRawValue['deliveryOption']>;
  user: FormControl<PurchaseOrderFormRawValue['user']>;
  productVariation: FormControl<PurchaseOrderFormRawValue['productVariation']>;
  customer: FormControl<PurchaseOrderFormRawValue['customer']>;
};

export type PurchaseOrderFormGroup = FormGroup<PurchaseOrderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PurchaseOrderFormService {
  createPurchaseOrderFormGroup(purchaseOrder: PurchaseOrderFormGroupInput = { id: null }): PurchaseOrderFormGroup {
    const purchaseOrderRawValue = this.convertPurchaseOrderToPurchaseOrderRawValue({
      ...this.getFormDefaults(),
      ...purchaseOrder,
    });
    return new FormGroup<PurchaseOrderFormGroupContent>({
      id: new FormControl(
        { value: purchaseOrderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      placedDate: new FormControl(purchaseOrderRawValue.placedDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(purchaseOrderRawValue.status, {
        validators: [Validators.required],
      }),
      code: new FormControl(purchaseOrderRawValue.code, {
        validators: [Validators.minLength(20), Validators.maxLength(250)],
      }),
      deliveryOption: new FormControl(purchaseOrderRawValue.deliveryOption, {
        validators: [Validators.required],
      }),
      user: new FormControl(purchaseOrderRawValue.user, {
        validators: [Validators.required],
      }),
      productVariation: new FormControl(purchaseOrderRawValue.productVariation, {
        validators: [Validators.required],
      }),
      customer: new FormControl(purchaseOrderRawValue.customer),
    });
  }

  getPurchaseOrder(form: PurchaseOrderFormGroup): IPurchaseOrder | NewPurchaseOrder {
    return this.convertPurchaseOrderRawValueToPurchaseOrder(form.getRawValue() as PurchaseOrderFormRawValue | NewPurchaseOrderFormRawValue);
  }

  resetForm(form: PurchaseOrderFormGroup, purchaseOrder: PurchaseOrderFormGroupInput): void {
    const purchaseOrderRawValue = this.convertPurchaseOrderToPurchaseOrderRawValue({ ...this.getFormDefaults(), ...purchaseOrder });
    form.reset(
      {
        ...purchaseOrderRawValue,
        id: { value: purchaseOrderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PurchaseOrderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      placedDate: currentTime,
    };
  }

  private convertPurchaseOrderRawValueToPurchaseOrder(
    rawPurchaseOrder: PurchaseOrderFormRawValue | NewPurchaseOrderFormRawValue
  ): IPurchaseOrder | NewPurchaseOrder {
    return {
      ...rawPurchaseOrder,
      placedDate: dayjs(rawPurchaseOrder.placedDate, DATE_TIME_FORMAT),
    };
  }

  private convertPurchaseOrderToPurchaseOrderRawValue(
    purchaseOrder: IPurchaseOrder | (Partial<NewPurchaseOrder> & PurchaseOrderFormDefaults)
  ): PurchaseOrderFormRawValue | PartialWithRequiredKeyOf<NewPurchaseOrderFormRawValue> {
    return {
      ...purchaseOrder,
      placedDate: purchaseOrder.placedDate ? purchaseOrder.placedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
