import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITaxRate, NewTaxRate } from '../tax-rate.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITaxRate for edit and NewTaxRateFormGroupInput for create.
 */
type TaxRateFormGroupInput = ITaxRate | PartialWithRequiredKeyOf<NewTaxRate>;

type TaxRateFormDefaults = Pick<NewTaxRate, 'id' | 'compound'>;

type TaxRateFormGroupContent = {
  id: FormControl<ITaxRate['id'] | NewTaxRate['id']>;
  country: FormControl<ITaxRate['country']>;
  state: FormControl<ITaxRate['state']>;
  postcode: FormControl<ITaxRate['postcode']>;
  city: FormControl<ITaxRate['city']>;
  rate: FormControl<ITaxRate['rate']>;
  name: FormControl<ITaxRate['name']>;
  compound: FormControl<ITaxRate['compound']>;
  priority: FormControl<ITaxRate['priority']>;
  taxClass: FormControl<ITaxRate['taxClass']>;
};

export type TaxRateFormGroup = FormGroup<TaxRateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TaxRateFormService {
  createTaxRateFormGroup(taxRate: TaxRateFormGroupInput = { id: null }): TaxRateFormGroup {
    const taxRateRawValue = {
      ...this.getFormDefaults(),
      ...taxRate,
    };
    return new FormGroup<TaxRateFormGroupContent>({
      id: new FormControl(
        { value: taxRateRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      country: new FormControl(taxRateRawValue.country, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(50)],
      }),
      state: new FormControl(taxRateRawValue.state, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(50)],
      }),
      postcode: new FormControl(taxRateRawValue.postcode, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(20)],
      }),
      city: new FormControl(taxRateRawValue.city, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(50)],
      }),
      rate: new FormControl(taxRateRawValue.rate, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(50)],
      }),
      name: new FormControl(taxRateRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(250)],
      }),
      compound: new FormControl(taxRateRawValue.compound, {
        validators: [Validators.required],
      }),
      priority: new FormControl(taxRateRawValue.priority, {
        validators: [Validators.required],
      }),
      taxClass: new FormControl(taxRateRawValue.taxClass, {
        validators: [Validators.required],
      }),
    });
  }

  getTaxRate(form: TaxRateFormGroup): ITaxRate | NewTaxRate {
    return form.getRawValue() as ITaxRate | NewTaxRate;
  }

  resetForm(form: TaxRateFormGroup, taxRate: TaxRateFormGroupInput): void {
    const taxRateRawValue = { ...this.getFormDefaults(), ...taxRate };
    form.reset(
      {
        ...taxRateRawValue,
        id: { value: taxRateRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TaxRateFormDefaults {
    return {
      id: null,
      compound: false,
    };
  }
}
