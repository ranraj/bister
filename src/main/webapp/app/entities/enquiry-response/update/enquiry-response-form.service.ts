import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEnquiryResponse, NewEnquiryResponse } from '../enquiry-response.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEnquiryResponse for edit and NewEnquiryResponseFormGroupInput for create.
 */
type EnquiryResponseFormGroupInput = IEnquiryResponse | PartialWithRequiredKeyOf<NewEnquiryResponse>;

type EnquiryResponseFormDefaults = Pick<NewEnquiryResponse, 'id'>;

type EnquiryResponseFormGroupContent = {
  id: FormControl<IEnquiryResponse['id'] | NewEnquiryResponse['id']>;
  query: FormControl<IEnquiryResponse['query']>;
  details: FormControl<IEnquiryResponse['details']>;
  enquiryResponseType: FormControl<IEnquiryResponse['enquiryResponseType']>;
  agent: FormControl<IEnquiryResponse['agent']>;
  enquiry: FormControl<IEnquiryResponse['enquiry']>;
};

export type EnquiryResponseFormGroup = FormGroup<EnquiryResponseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EnquiryResponseFormService {
  createEnquiryResponseFormGroup(enquiryResponse: EnquiryResponseFormGroupInput = { id: null }): EnquiryResponseFormGroup {
    const enquiryResponseRawValue = {
      ...this.getFormDefaults(),
      ...enquiryResponse,
    };
    return new FormGroup<EnquiryResponseFormGroupContent>({
      id: new FormControl(
        { value: enquiryResponseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      query: new FormControl(enquiryResponseRawValue.query, {
        validators: [Validators.minLength(3), Validators.maxLength(1000)],
      }),
      details: new FormControl(enquiryResponseRawValue.details, {
        validators: [Validators.minLength(3), Validators.maxLength(1000)],
      }),
      enquiryResponseType: new FormControl(enquiryResponseRawValue.enquiryResponseType, {
        validators: [Validators.required],
      }),
      agent: new FormControl(enquiryResponseRawValue.agent),
      enquiry: new FormControl(enquiryResponseRawValue.enquiry),
    });
  }

  getEnquiryResponse(form: EnquiryResponseFormGroup): IEnquiryResponse | NewEnquiryResponse {
    return form.getRawValue() as IEnquiryResponse | NewEnquiryResponse;
  }

  resetForm(form: EnquiryResponseFormGroup, enquiryResponse: EnquiryResponseFormGroupInput): void {
    const enquiryResponseRawValue = { ...this.getFormDefaults(), ...enquiryResponse };
    form.reset(
      {
        ...enquiryResponseRawValue,
        id: { value: enquiryResponseRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EnquiryResponseFormDefaults {
    return {
      id: null,
    };
  }
}
