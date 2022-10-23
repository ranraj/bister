import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITaxClass, NewTaxClass } from '../tax-class.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITaxClass for edit and NewTaxClassFormGroupInput for create.
 */
type TaxClassFormGroupInput = ITaxClass | PartialWithRequiredKeyOf<NewTaxClass>;

type TaxClassFormDefaults = Pick<NewTaxClass, 'id'>;

type TaxClassFormGroupContent = {
  id: FormControl<ITaxClass['id'] | NewTaxClass['id']>;
  name: FormControl<ITaxClass['name']>;
  slug: FormControl<ITaxClass['slug']>;
};

export type TaxClassFormGroup = FormGroup<TaxClassFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TaxClassFormService {
  createTaxClassFormGroup(taxClass: TaxClassFormGroupInput = { id: null }): TaxClassFormGroup {
    const taxClassRawValue = {
      ...this.getFormDefaults(),
      ...taxClass,
    };
    return new FormGroup<TaxClassFormGroupContent>({
      id: new FormControl(
        { value: taxClassRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(taxClassRawValue.name, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
      }),
      slug: new FormControl(taxClassRawValue.slug, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
      }),
    });
  }

  getTaxClass(form: TaxClassFormGroup): ITaxClass | NewTaxClass {
    return form.getRawValue() as ITaxClass | NewTaxClass;
  }

  resetForm(form: TaxClassFormGroup, taxClass: TaxClassFormGroupInput): void {
    const taxClassRawValue = { ...this.getFormDefaults(), ...taxClass };
    form.reset(
      {
        ...taxClassRawValue,
        id: { value: taxClassRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TaxClassFormDefaults {
    return {
      id: null,
    };
  }
}
