import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAddress, NewAddress } from '../address.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAddress for edit and NewAddressFormGroupInput for create.
 */
type AddressFormGroupInput = IAddress | PartialWithRequiredKeyOf<NewAddress>;

type AddressFormDefaults = Pick<NewAddress, 'id'>;

type AddressFormGroupContent = {
  id: FormControl<IAddress['id'] | NewAddress['id']>;
  name: FormControl<IAddress['name']>;
  addressLine1: FormControl<IAddress['addressLine1']>;
  addressLine2: FormControl<IAddress['addressLine2']>;
  landmark: FormControl<IAddress['landmark']>;
  city: FormControl<IAddress['city']>;
  state: FormControl<IAddress['state']>;
  country: FormControl<IAddress['country']>;
  postcode: FormControl<IAddress['postcode']>;
  latitude: FormControl<IAddress['latitude']>;
  longitude: FormControl<IAddress['longitude']>;
  addressType: FormControl<IAddress['addressType']>;
};

export type AddressFormGroup = FormGroup<AddressFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AddressFormService {
  createAddressFormGroup(address: AddressFormGroupInput = { id: null }): AddressFormGroup {
    const addressRawValue = {
      ...this.getFormDefaults(),
      ...address,
    };
    return new FormGroup<AddressFormGroupContent>({
      id: new FormControl(
        { value: addressRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(addressRawValue.name, {
        validators: [Validators.required, Validators.minLength(20), Validators.maxLength(250)],
      }),
      addressLine1: new FormControl(addressRawValue.addressLine1, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(250)],
      }),
      addressLine2: new FormControl(addressRawValue.addressLine2, {
        validators: [Validators.minLength(3), Validators.maxLength(250)],
      }),
      landmark: new FormControl(addressRawValue.landmark, {
        validators: [Validators.minLength(3), Validators.maxLength(250)],
      }),
      city: new FormControl(addressRawValue.city, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      state: new FormControl(addressRawValue.state, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      country: new FormControl(addressRawValue.country, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      postcode: new FormControl(addressRawValue.postcode, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(20)],
      }),
      latitude: new FormControl(addressRawValue.latitude, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      longitude: new FormControl(addressRawValue.longitude, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      addressType: new FormControl(addressRawValue.addressType, {
        validators: [Validators.required],
      }),
    });
  }

  getAddress(form: AddressFormGroup): IAddress | NewAddress {
    return form.getRawValue() as IAddress | NewAddress;
  }

  resetForm(form: AddressFormGroup, address: AddressFormGroupInput): void {
    const addressRawValue = { ...this.getFormDefaults(), ...address };
    form.reset(
      {
        ...addressRawValue,
        id: { value: addressRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AddressFormDefaults {
    return {
      id: null,
    };
  }
}
