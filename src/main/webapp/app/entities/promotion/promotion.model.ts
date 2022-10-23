import dayjs from 'dayjs/esm';
import { PromotionContentType } from 'app/entities/enumerations/promotion-content-type.model';

export interface IPromotion {
  id: number;
  productId?: number | null;
  projectId?: number | null;
  contentType?: PromotionContentType | null;
  recipients?: string | null;
  recipientGroup?: string | null;
  createdBy?: number | null;
  createdAt?: dayjs.Dayjs | null;
  sendAt?: dayjs.Dayjs | null;
  attachmentId?: number | null;
}

export type NewPromotion = Omit<IPromotion, 'id'> & { id: null };
