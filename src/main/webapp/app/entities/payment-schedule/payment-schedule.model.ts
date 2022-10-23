import dayjs from 'dayjs/esm';
import { IInvoice } from 'app/entities/invoice/invoice.model';
import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { PaymentScheduleStatus } from 'app/entities/enumerations/payment-schedule-status.model';

export interface IPaymentSchedule {
  id: number;
  dueDate?: dayjs.Dayjs | null;
  totalPrice?: number | null;
  remarks?: string | null;
  status?: PaymentScheduleStatus | null;
  isOverDue?: boolean | null;
  invoice?: Pick<IInvoice, 'id'> | null;
  purchaseOrdep?: Pick<IPurchaseOrder, 'id'> | null;
}

export type NewPaymentSchedule = Omit<IPaymentSchedule, 'id'> & { id: null };
