import dayjs from 'dayjs/esm';
import { IAgent } from 'app/entities/agent/agent.model';
import { IUser } from 'app/entities/user/user.model';
import { NotificationSourceType } from 'app/entities/enumerations/notification-source-type.model';
import { NotificationType } from 'app/entities/enumerations/notification-type.model';
import { NotificationMode } from 'app/entities/enumerations/notification-mode.model';

export interface INotification {
  id: number;
  date?: dayjs.Dayjs | null;
  details?: string | null;
  sentDate?: dayjs.Dayjs | null;
  googleNotificationId?: string | null;
  whatsappNotificationId?: string | null;
  smsNotificationId?: string | null;
  productId?: number | null;
  projectId?: number | null;
  scheduleId?: number | null;
  promotionId?: number | null;
  read?: boolean | null;
  notificationSourceType?: NotificationSourceType | null;
  notificationType?: NotificationType | null;
  notificationMode?: NotificationMode | null;
  agent?: Pick<IAgent, 'id'> | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
