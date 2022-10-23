import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICertification, NewCertification } from '../certification.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICertification for edit and NewCertificationFormGroupInput for create.
 */
type CertificationFormGroupInput = ICertification | PartialWithRequiredKeyOf<NewCertification>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICertification | NewCertification> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type CertificationFormRawValue = FormValueOf<ICertification>;

type NewCertificationFormRawValue = FormValueOf<NewCertification>;

type CertificationFormDefaults = Pick<NewCertification, 'id' | 'createdAt'>;

type CertificationFormGroupContent = {
  id: FormControl<CertificationFormRawValue['id'] | NewCertification['id']>;
  name: FormControl<CertificationFormRawValue['name']>;
  slug: FormControl<CertificationFormRawValue['slug']>;
  authority: FormControl<CertificationFormRawValue['authority']>;
  status: FormControl<CertificationFormRawValue['status']>;
  projectId: FormControl<CertificationFormRawValue['projectId']>;
  prodcut: FormControl<CertificationFormRawValue['prodcut']>;
  orgId: FormControl<CertificationFormRawValue['orgId']>;
  facitlityId: FormControl<CertificationFormRawValue['facitlityId']>;
  createdBy: FormControl<CertificationFormRawValue['createdBy']>;
  createdAt: FormControl<CertificationFormRawValue['createdAt']>;
};

export type CertificationFormGroup = FormGroup<CertificationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CertificationFormService {
  createCertificationFormGroup(certification: CertificationFormGroupInput = { id: null }): CertificationFormGroup {
    const certificationRawValue = this.convertCertificationToCertificationRawValue({
      ...this.getFormDefaults(),
      ...certification,
    });
    return new FormGroup<CertificationFormGroupContent>({
      id: new FormControl(
        { value: certificationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(certificationRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      slug: new FormControl(certificationRawValue.slug, {
        validators: [Validators.minLength(2), Validators.maxLength(100)],
      }),
      authority: new FormControl(certificationRawValue.authority, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100)],
      }),
      status: new FormControl(certificationRawValue.status, {
        validators: [Validators.required],
      }),
      projectId: new FormControl(certificationRawValue.projectId),
      prodcut: new FormControl(certificationRawValue.prodcut),
      orgId: new FormControl(certificationRawValue.orgId),
      facitlityId: new FormControl(certificationRawValue.facitlityId),
      createdBy: new FormControl(certificationRawValue.createdBy, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(certificationRawValue.createdAt, {
        validators: [Validators.required],
      }),
    });
  }

  getCertification(form: CertificationFormGroup): ICertification | NewCertification {
    return this.convertCertificationRawValueToCertification(form.getRawValue() as CertificationFormRawValue | NewCertificationFormRawValue);
  }

  resetForm(form: CertificationFormGroup, certification: CertificationFormGroupInput): void {
    const certificationRawValue = this.convertCertificationToCertificationRawValue({ ...this.getFormDefaults(), ...certification });
    form.reset(
      {
        ...certificationRawValue,
        id: { value: certificationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CertificationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertCertificationRawValueToCertification(
    rawCertification: CertificationFormRawValue | NewCertificationFormRawValue
  ): ICertification | NewCertification {
    return {
      ...rawCertification,
      createdAt: dayjs(rawCertification.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertCertificationToCertificationRawValue(
    certification: ICertification | (Partial<NewCertification> & CertificationFormDefaults)
  ): CertificationFormRawValue | PartialWithRequiredKeyOf<NewCertificationFormRawValue> {
    return {
      ...certification,
      createdAt: certification.createdAt ? certification.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
