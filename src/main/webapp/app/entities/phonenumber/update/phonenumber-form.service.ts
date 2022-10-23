import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPhonenumber, NewPhonenumber } from '../phonenumber.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPhonenumber for edit and NewPhonenumberFormGroupInput for create.
 */
type PhonenumberFormGroupInput = IPhonenumber | PartialWithRequiredKeyOf<NewPhonenumber>;

type PhonenumberFormDefaults = Pick<NewPhonenumber, 'id'>;

type PhonenumberFormGroupContent = {
  id: FormControl<IPhonenumber['id'] | NewPhonenumber['id']>;
  country: FormControl<IPhonenumber['country']>;
  code: FormControl<IPhonenumber['code']>;
  contactNumber: FormControl<IPhonenumber['contactNumber']>;
  phonenumberType: FormControl<IPhonenumber['phonenumberType']>;
  user: FormControl<IPhonenumber['user']>;
  organisation: FormControl<IPhonenumber['organisation']>;
  facility: FormControl<IPhonenumber['facility']>;
};

export type PhonenumberFormGroup = FormGroup<PhonenumberFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PhonenumberFormService {
  createPhonenumberFormGroup(phonenumber: PhonenumberFormGroupInput = { id: null }): PhonenumberFormGroup {
    const phonenumberRawValue = {
      ...this.getFormDefaults(),
      ...phonenumber,
    };
    return new FormGroup<PhonenumberFormGroupContent>({
      id: new FormControl(
        { value: phonenumberRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      country: new FormControl(phonenumberRawValue.country, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      code: new FormControl(phonenumberRawValue.code, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(10)],
      }),
      contactNumber: new FormControl(phonenumberRawValue.contactNumber, {
        validators: [Validators.required, Validators.minLength(10), Validators.maxLength(15)],
      }),
      phonenumberType: new FormControl(phonenumberRawValue.phonenumberType, {
        validators: [Validators.required],
      }),
      user: new FormControl(phonenumberRawValue.user),
      organisation: new FormControl(phonenumberRawValue.organisation),
      facility: new FormControl(phonenumberRawValue.facility),
    });
  }

  getPhonenumber(form: PhonenumberFormGroup): IPhonenumber | NewPhonenumber {
    return form.getRawValue() as IPhonenumber | NewPhonenumber;
  }

  resetForm(form: PhonenumberFormGroup, phonenumber: PhonenumberFormGroupInput): void {
    const phonenumberRawValue = { ...this.getFormDefaults(), ...phonenumber };
    form.reset(
      {
        ...phonenumberRawValue,
        id: { value: phonenumberRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PhonenumberFormDefaults {
    return {
      id: null,
    };
  }
}
