import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProductActivity, NewProductActivity } from '../product-activity.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductActivity for edit and NewProductActivityFormGroupInput for create.
 */
type ProductActivityFormGroupInput = IProductActivity | PartialWithRequiredKeyOf<NewProductActivity>;

type ProductActivityFormDefaults = Pick<NewProductActivity, 'id'>;

type ProductActivityFormGroupContent = {
  id: FormControl<IProductActivity['id'] | NewProductActivity['id']>;
  title: FormControl<IProductActivity['title']>;
  details: FormControl<IProductActivity['details']>;
  status: FormControl<IProductActivity['status']>;
  product: FormControl<IProductActivity['product']>;
};

export type ProductActivityFormGroup = FormGroup<ProductActivityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductActivityFormService {
  createProductActivityFormGroup(productActivity: ProductActivityFormGroupInput = { id: null }): ProductActivityFormGroup {
    const productActivityRawValue = {
      ...this.getFormDefaults(),
      ...productActivity,
    };
    return new FormGroup<ProductActivityFormGroupContent>({
      id: new FormControl(
        { value: productActivityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(productActivityRawValue.title, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100)],
      }),
      details: new FormControl(productActivityRawValue.details, {
        validators: [Validators.minLength(20), Validators.maxLength(250)],
      }),
      status: new FormControl(productActivityRawValue.status, {
        validators: [Validators.required],
      }),
      product: new FormControl(productActivityRawValue.product, {
        validators: [Validators.required],
      }),
    });
  }

  getProductActivity(form: ProductActivityFormGroup): IProductActivity | NewProductActivity {
    return form.getRawValue() as IProductActivity | NewProductActivity;
  }

  resetForm(form: ProductActivityFormGroup, productActivity: ProductActivityFormGroupInput): void {
    const productActivityRawValue = { ...this.getFormDefaults(), ...productActivity };
    form.reset(
      {
        ...productActivityRawValue,
        id: { value: productActivityRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductActivityFormDefaults {
    return {
      id: null,
    };
  }
}
