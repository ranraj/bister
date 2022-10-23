import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPromotion, NewPromotion } from '../promotion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPromotion for edit and NewPromotionFormGroupInput for create.
 */
type PromotionFormGroupInput = IPromotion | PartialWithRequiredKeyOf<NewPromotion>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPromotion | NewPromotion> = Omit<T, 'createdAt' | 'sendAt'> & {
  createdAt?: string | null;
  sendAt?: string | null;
};

type PromotionFormRawValue = FormValueOf<IPromotion>;

type NewPromotionFormRawValue = FormValueOf<NewPromotion>;

type PromotionFormDefaults = Pick<NewPromotion, 'id' | 'createdAt' | 'sendAt'>;

type PromotionFormGroupContent = {
  id: FormControl<PromotionFormRawValue['id'] | NewPromotion['id']>;
  productId: FormControl<PromotionFormRawValue['productId']>;
  projectId: FormControl<PromotionFormRawValue['projectId']>;
  contentType: FormControl<PromotionFormRawValue['contentType']>;
  recipients: FormControl<PromotionFormRawValue['recipients']>;
  recipientGroup: FormControl<PromotionFormRawValue['recipientGroup']>;
  createdBy: FormControl<PromotionFormRawValue['createdBy']>;
  createdAt: FormControl<PromotionFormRawValue['createdAt']>;
  sendAt: FormControl<PromotionFormRawValue['sendAt']>;
  attachmentId: FormControl<PromotionFormRawValue['attachmentId']>;
};

export type PromotionFormGroup = FormGroup<PromotionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PromotionFormService {
  createPromotionFormGroup(promotion: PromotionFormGroupInput = { id: null }): PromotionFormGroup {
    const promotionRawValue = this.convertPromotionToPromotionRawValue({
      ...this.getFormDefaults(),
      ...promotion,
    });
    return new FormGroup<PromotionFormGroupContent>({
      id: new FormControl(
        { value: promotionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      productId: new FormControl(promotionRawValue.productId),
      projectId: new FormControl(promotionRawValue.projectId),
      contentType: new FormControl(promotionRawValue.contentType, {
        validators: [Validators.required],
      }),
      recipients: new FormControl(promotionRawValue.recipients),
      recipientGroup: new FormControl(promotionRawValue.recipientGroup),
      createdBy: new FormControl(promotionRawValue.createdBy),
      createdAt: new FormControl(promotionRawValue.createdAt, {
        validators: [Validators.required],
      }),
      sendAt: new FormControl(promotionRawValue.sendAt, {
        validators: [Validators.required],
      }),
      attachmentId: new FormControl(promotionRawValue.attachmentId),
    });
  }

  getPromotion(form: PromotionFormGroup): IPromotion | NewPromotion {
    return this.convertPromotionRawValueToPromotion(form.getRawValue() as PromotionFormRawValue | NewPromotionFormRawValue);
  }

  resetForm(form: PromotionFormGroup, promotion: PromotionFormGroupInput): void {
    const promotionRawValue = this.convertPromotionToPromotionRawValue({ ...this.getFormDefaults(), ...promotion });
    form.reset(
      {
        ...promotionRawValue,
        id: { value: promotionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PromotionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      sendAt: currentTime,
    };
  }

  private convertPromotionRawValueToPromotion(rawPromotion: PromotionFormRawValue | NewPromotionFormRawValue): IPromotion | NewPromotion {
    return {
      ...rawPromotion,
      createdAt: dayjs(rawPromotion.createdAt, DATE_TIME_FORMAT),
      sendAt: dayjs(rawPromotion.sendAt, DATE_TIME_FORMAT),
    };
  }

  private convertPromotionToPromotionRawValue(
    promotion: IPromotion | (Partial<NewPromotion> & PromotionFormDefaults)
  ): PromotionFormRawValue | PartialWithRequiredKeyOf<NewPromotionFormRawValue> {
    return {
      ...promotion,
      createdAt: promotion.createdAt ? promotion.createdAt.format(DATE_TIME_FORMAT) : undefined,
      sendAt: promotion.sendAt ? promotion.sendAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
