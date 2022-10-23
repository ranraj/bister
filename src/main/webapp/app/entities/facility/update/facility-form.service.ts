import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFacility, NewFacility } from '../facility.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFacility for edit and NewFacilityFormGroupInput for create.
 */
type FacilityFormGroupInput = IFacility | PartialWithRequiredKeyOf<NewFacility>;

type FacilityFormDefaults = Pick<NewFacility, 'id'>;

type FacilityFormGroupContent = {
  id: FormControl<IFacility['id'] | NewFacility['id']>;
  name: FormControl<IFacility['name']>;
  description: FormControl<IFacility['description']>;
  facilityType: FormControl<IFacility['facilityType']>;
  address: FormControl<IFacility['address']>;
  user: FormControl<IFacility['user']>;
  organisation: FormControl<IFacility['organisation']>;
};

export type FacilityFormGroup = FormGroup<FacilityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FacilityFormService {
  createFacilityFormGroup(facility: FacilityFormGroupInput = { id: null }): FacilityFormGroup {
    const facilityRawValue = {
      ...this.getFormDefaults(),
      ...facility,
    };
    return new FormGroup<FacilityFormGroupContent>({
      id: new FormControl(
        { value: facilityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(facilityRawValue.name, {
        validators: [Validators.required, Validators.minLength(1), Validators.maxLength(250)],
      }),
      description: new FormControl(facilityRawValue.description, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(250)],
      }),
      facilityType: new FormControl(facilityRawValue.facilityType, {
        validators: [Validators.required],
      }),
      address: new FormControl(facilityRawValue.address, {
        validators: [Validators.required],
      }),
      user: new FormControl(facilityRawValue.user, {
        validators: [Validators.required],
      }),
      organisation: new FormControl(facilityRawValue.organisation, {
        validators: [Validators.required],
      }),
    });
  }

  getFacility(form: FacilityFormGroup): IFacility | NewFacility {
    return form.getRawValue() as IFacility | NewFacility;
  }

  resetForm(form: FacilityFormGroup, facility: FacilityFormGroupInput): void {
    const facilityRawValue = { ...this.getFormDefaults(), ...facility };
    form.reset(
      {
        ...facilityRawValue,
        id: { value: facilityRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FacilityFormDefaults {
    return {
      id: null,
    };
  }
}
