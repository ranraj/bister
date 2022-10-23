import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IProductVariation } from 'app/entities/product-variation/product-variation.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';
import { DeliveryOption } from 'app/entities/enumerations/delivery-option.model';

export interface IPurchaseOrder {
  id: number;
  placedDate?: dayjs.Dayjs | null;
  status?: OrderStatus | null;
  code?: string | null;
  deliveryOption?: DeliveryOption | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  productVariation?: Pick<IProductVariation, 'id' | 'name'> | null;
}

export type NewPurchaseOrder = Omit<IPurchaseOrder, 'id'> & { id: null };
