import dayjs from 'dayjs/esm';

import { NotificationSourceType } from 'app/entities/enumerations/notification-source-type.model';
import { NotificationType } from 'app/entities/enumerations/notification-type.model';
import { NotificationMode } from 'app/entities/enumerations/notification-mode.model';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 30621,
  date: dayjs('2022-10-28T14:32'),
  sentDate: dayjs('2022-10-28T07:25'),
  read: true,
  notificationSourceType: NotificationSourceType['ENQUIRY'],
  notificationMode: NotificationMode['COURIER'],
};

export const sampleWithPartialData: INotification = {
  id: 14097,
  date: dayjs('2022-10-28T02:39'),
  details: 'Garden quantify',
  sentDate: dayjs('2022-10-28T10:11'),
  promotionId: 67933,
  read: true,
  notificationSourceType: NotificationSourceType['PRODUCT'],
  notificationMode: NotificationMode['EMAIL'],
};

export const sampleWithFullData: INotification = {
  id: 32316,
  date: dayjs('2022-10-28T04:56'),
  details: 'generate Buckinghamshire',
  sentDate: dayjs('2022-10-27T17:54'),
  googleNotificationId: 'Market hacking Wyoming',
  whatsappNotificationId: 'tan',
  smsNotificationId: 'Gorgeous Savings',
  productId: 37922,
  projectId: 97151,
  scheduleId: 94050,
  promotionId: 25648,
  read: false,
  notificationSourceType: NotificationSourceType['REMINDER'],
  notificationType: NotificationType['DIGITAL'],
  notificationMode: NotificationMode['WHATSAPP_CALL'],
};

export const sampleWithNewData: NewNotification = {
  date: dayjs('2022-10-28T14:27'),
  sentDate: dayjs('2022-10-28T14:36'),
  read: false,
  notificationSourceType: NotificationSourceType['SCHEDULE'],
  notificationMode: NotificationMode['WHATSAPP_MESSAGE'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
