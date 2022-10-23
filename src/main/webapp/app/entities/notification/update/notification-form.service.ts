import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { INotification, NewNotification } from '../notification.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts INotification for edit and NewNotificationFormGroupInput for create.
 */
type NotificationFormGroupInput = INotification | PartialWithRequiredKeyOf<NewNotification>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends INotification | NewNotification> = Omit<T, 'date' | 'sentDate'> & {
  date?: string | null;
  sentDate?: string | null;
};

type NotificationFormRawValue = FormValueOf<INotification>;

type NewNotificationFormRawValue = FormValueOf<NewNotification>;

type NotificationFormDefaults = Pick<NewNotification, 'id' | 'date' | 'sentDate' | 'read'>;

type NotificationFormGroupContent = {
  id: FormControl<NotificationFormRawValue['id'] | NewNotification['id']>;
  date: FormControl<NotificationFormRawValue['date']>;
  details: FormControl<NotificationFormRawValue['details']>;
  sentDate: FormControl<NotificationFormRawValue['sentDate']>;
  googleNotificationId: FormControl<NotificationFormRawValue['googleNotificationId']>;
  whatsappNotificationId: FormControl<NotificationFormRawValue['whatsappNotificationId']>;
  smsNotificationId: FormControl<NotificationFormRawValue['smsNotificationId']>;
  productId: FormControl<NotificationFormRawValue['productId']>;
  projectId: FormControl<NotificationFormRawValue['projectId']>;
  scheduleId: FormControl<NotificationFormRawValue['scheduleId']>;
  promotionId: FormControl<NotificationFormRawValue['promotionId']>;
  read: FormControl<NotificationFormRawValue['read']>;
  notificationSourceType: FormControl<NotificationFormRawValue['notificationSourceType']>;
  notificationType: FormControl<NotificationFormRawValue['notificationType']>;
  notificationMode: FormControl<NotificationFormRawValue['notificationMode']>;
  agent: FormControl<NotificationFormRawValue['agent']>;
  user: FormControl<NotificationFormRawValue['user']>;
};

export type NotificationFormGroup = FormGroup<NotificationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class NotificationFormService {
  createNotificationFormGroup(notification: NotificationFormGroupInput = { id: null }): NotificationFormGroup {
    const notificationRawValue = this.convertNotificationToNotificationRawValue({
      ...this.getFormDefaults(),
      ...notification,
    });
    return new FormGroup<NotificationFormGroupContent>({
      id: new FormControl(
        { value: notificationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      date: new FormControl(notificationRawValue.date, {
        validators: [Validators.required],
      }),
      details: new FormControl(notificationRawValue.details, {
        validators: [Validators.minLength(3), Validators.maxLength(1000)],
      }),
      sentDate: new FormControl(notificationRawValue.sentDate, {
        validators: [Validators.required],
      }),
      googleNotificationId: new FormControl(notificationRawValue.googleNotificationId, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      whatsappNotificationId: new FormControl(notificationRawValue.whatsappNotificationId, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      smsNotificationId: new FormControl(notificationRawValue.smsNotificationId, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      productId: new FormControl(notificationRawValue.productId),
      projectId: new FormControl(notificationRawValue.projectId),
      scheduleId: new FormControl(notificationRawValue.scheduleId),
      promotionId: new FormControl(notificationRawValue.promotionId),
      read: new FormControl(notificationRawValue.read, {
        validators: [Validators.required],
      }),
      notificationSourceType: new FormControl(notificationRawValue.notificationSourceType, {
        validators: [Validators.required],
      }),
      notificationType: new FormControl(notificationRawValue.notificationType),
      notificationMode: new FormControl(notificationRawValue.notificationMode, {
        validators: [Validators.required],
      }),
      agent: new FormControl(notificationRawValue.agent),
      user: new FormControl(notificationRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getNotification(form: NotificationFormGroup): INotification | NewNotification {
    return this.convertNotificationRawValueToNotification(form.getRawValue() as NotificationFormRawValue | NewNotificationFormRawValue);
  }

  resetForm(form: NotificationFormGroup, notification: NotificationFormGroupInput): void {
    const notificationRawValue = this.convertNotificationToNotificationRawValue({ ...this.getFormDefaults(), ...notification });
    form.reset(
      {
        ...notificationRawValue,
        id: { value: notificationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): NotificationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      sentDate: currentTime,
      read: false,
    };
  }

  private convertNotificationRawValueToNotification(
    rawNotification: NotificationFormRawValue | NewNotificationFormRawValue
  ): INotification | NewNotification {
    return {
      ...rawNotification,
      date: dayjs(rawNotification.date, DATE_TIME_FORMAT),
      sentDate: dayjs(rawNotification.sentDate, DATE_TIME_FORMAT),
    };
  }

  private convertNotificationToNotificationRawValue(
    notification: INotification | (Partial<NewNotification> & NotificationFormDefaults)
  ): NotificationFormRawValue | PartialWithRequiredKeyOf<NewNotificationFormRawValue> {
    return {
      ...notification,
      date: notification.date ? notification.date.format(DATE_TIME_FORMAT) : undefined,
      sentDate: notification.sentDate ? notification.sentDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
