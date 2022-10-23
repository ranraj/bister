import dayjs from 'dayjs/esm';
import { ICustomer } from 'app/entities/customer/customer.model';
import { IProductVariation } from 'app/entities/product-variation/product-variation.model';
import { BookingOrderStatus } from 'app/entities/enumerations/booking-order-status.model';

export interface IBookingOrder {
  id: number;
  placedDate?: dayjs.Dayjs | null;
  status?: BookingOrderStatus | null;
  code?: string | null;
  bookingExpieryDate?: dayjs.Dayjs | null;
  customer?: Pick<ICustomer, 'id' | 'name'> | null;
  productVariation?: Pick<IProductVariation, 'id' | 'name'> | null;
}

export type NewBookingOrder = Omit<IBookingOrder, 'id'> & { id: null };
