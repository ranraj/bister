import dayjs from 'dayjs/esm';
import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { InvoiceStatus } from 'app/entities/enumerations/invoice-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

export interface IInvoice {
  id: number;
  code?: string | null;
  date?: dayjs.Dayjs | null;
  details?: string | null;
  status?: InvoiceStatus | null;
  paymentMethod?: PaymentMethod | null;
  paymentDate?: dayjs.Dayjs | null;
  paymentAmount?: number | null;
  purchaseOrder?: Pick<IPurchaseOrder, 'id'> | null;
}

export type NewInvoice = Omit<IInvoice, 'id'> & { id: null };
