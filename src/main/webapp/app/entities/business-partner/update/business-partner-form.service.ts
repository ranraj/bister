import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBusinessPartner, NewBusinessPartner } from '../business-partner.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBusinessPartner for edit and NewBusinessPartnerFormGroupInput for create.
 */
type BusinessPartnerFormGroupInput = IBusinessPartner | PartialWithRequiredKeyOf<NewBusinessPartner>;

type BusinessPartnerFormDefaults = Pick<NewBusinessPartner, 'id'>;

type BusinessPartnerFormGroupContent = {
  id: FormControl<IBusinessPartner['id'] | NewBusinessPartner['id']>;
  name: FormControl<IBusinessPartner['name']>;
  description: FormControl<IBusinessPartner['description']>;
  key: FormControl<IBusinessPartner['key']>;
};

export type BusinessPartnerFormGroup = FormGroup<BusinessPartnerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BusinessPartnerFormService {
  createBusinessPartnerFormGroup(businessPartner: BusinessPartnerFormGroupInput = { id: null }): BusinessPartnerFormGroup {
    const businessPartnerRawValue = {
      ...this.getFormDefaults(),
      ...businessPartner,
    };
    return new FormGroup<BusinessPartnerFormGroupContent>({
      id: new FormControl(
        { value: businessPartnerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(businessPartnerRawValue.name, {
        validators: [Validators.required, Validators.minLength(1), Validators.maxLength(250)],
      }),
      description: new FormControl(businessPartnerRawValue.description, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(250)],
      }),
      key: new FormControl(businessPartnerRawValue.key, {
        validators: [Validators.required],
      }),
    });
  }

  getBusinessPartner(form: BusinessPartnerFormGroup): IBusinessPartner | NewBusinessPartner {
    return form.getRawValue() as IBusinessPartner | NewBusinessPartner;
  }

  resetForm(form: BusinessPartnerFormGroup, businessPartner: BusinessPartnerFormGroupInput): void {
    const businessPartnerRawValue = { ...this.getFormDefaults(), ...businessPartner };
    form.reset(
      {
        ...businessPartnerRawValue,
        id: { value: businessPartnerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BusinessPartnerFormDefaults {
    return {
      id: null,
    };
  }
}
