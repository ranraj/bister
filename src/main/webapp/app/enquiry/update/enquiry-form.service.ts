import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEnquiry, NewEnquiry } from '../enquiry.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEnquiry for edit and NewEnquiryFormGroupInput for create.
 */
type EnquiryFormGroupInput = IEnquiry | PartialWithRequiredKeyOf<NewEnquiry>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEnquiry | NewEnquiry> = Omit<T, 'raisedDate' | 'lastResponseDate'> & {
  raisedDate?: string | null;
  lastResponseDate?: string | null;
};

type EnquiryFormRawValue = FormValueOf<IEnquiry>;

type NewEnquiryFormRawValue = FormValueOf<NewEnquiry>;

type EnquiryFormDefaults = Pick<NewEnquiry, 'id' | 'raisedDate' | 'lastResponseDate'>;

type EnquiryFormGroupContent = {
  id: FormControl<EnquiryFormRawValue['id'] | NewEnquiry['id']>;
  raisedDate: FormControl<EnquiryFormRawValue['raisedDate']>;
  subject: FormControl<EnquiryFormRawValue['subject']>;
  details: FormControl<EnquiryFormRawValue['details']>;
  lastResponseDate: FormControl<EnquiryFormRawValue['lastResponseDate']>;
  lastResponseId: FormControl<EnquiryFormRawValue['lastResponseId']>;
  enquiryType: FormControl<EnquiryFormRawValue['enquiryType']>;
  status: FormControl<EnquiryFormRawValue['status']>;
  agent: FormControl<EnquiryFormRawValue['agent']>;
  project: FormControl<EnquiryFormRawValue['project']>;
  product: FormControl<EnquiryFormRawValue['product']>;
  customer: FormControl<EnquiryFormRawValue['customer']>;
};

export type EnquiryFormGroup = FormGroup<EnquiryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EnquiryFormService {
  createEnquiryFormGroup(enquiry: EnquiryFormGroupInput = { id: null }): EnquiryFormGroup {
    const enquiryRawValue = this.convertEnquiryToEnquiryRawValue({
      ...this.getFormDefaults(),
      ...enquiry,
    });
    return new FormGroup<EnquiryFormGroupContent>({
      id: new FormControl(
        { value: enquiryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      raisedDate: new FormControl(enquiryRawValue.raisedDate, {
        validators: [Validators.required],
      }),
      subject: new FormControl(enquiryRawValue.subject, {
        validators: [Validators.required],
      }),
      details: new FormControl(enquiryRawValue.details, {
        validators: [Validators.minLength(3), Validators.maxLength(1000)],
      }),
      lastResponseDate: new FormControl(enquiryRawValue.lastResponseDate),
      lastResponseId: new FormControl(enquiryRawValue.lastResponseId),
      enquiryType: new FormControl(enquiryRawValue.enquiryType, {
        validators: [Validators.required],
      }),
      status: new FormControl(enquiryRawValue.status),
      agent: new FormControl(enquiryRawValue.agent),
      project: new FormControl(enquiryRawValue.project),
      product: new FormControl(enquiryRawValue.product),
      customer: new FormControl(enquiryRawValue.customer),
    });
  }

  getEnquiry(form: EnquiryFormGroup): IEnquiry | NewEnquiry {
    return this.convertEnquiryRawValueToEnquiry(form.getRawValue() as EnquiryFormRawValue | NewEnquiryFormRawValue);
  }

  resetForm(form: EnquiryFormGroup, enquiry: EnquiryFormGroupInput): void {
    const enquiryRawValue = this.convertEnquiryToEnquiryRawValue({ ...this.getFormDefaults(), ...enquiry });
    form.reset(
      {
        ...enquiryRawValue,
        id: { value: enquiryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EnquiryFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      raisedDate: currentTime,
      lastResponseDate: currentTime,
    };
  }

  private convertEnquiryRawValueToEnquiry(rawEnquiry: EnquiryFormRawValue | NewEnquiryFormRawValue): IEnquiry | NewEnquiry {
    return {
      ...rawEnquiry,
      raisedDate: dayjs(rawEnquiry.raisedDate, DATE_TIME_FORMAT),
      lastResponseDate: dayjs(rawEnquiry.lastResponseDate, DATE_TIME_FORMAT),
    };
  }

  private convertEnquiryToEnquiryRawValue(
    enquiry: IEnquiry | (Partial<NewEnquiry> & EnquiryFormDefaults)
  ): EnquiryFormRawValue | PartialWithRequiredKeyOf<NewEnquiryFormRawValue> {
    return {
      ...enquiry,
      raisedDate: enquiry.raisedDate ? enquiry.raisedDate.format(DATE_TIME_FORMAT) : undefined,
      lastResponseDate: enquiry.lastResponseDate ? enquiry.lastResponseDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
