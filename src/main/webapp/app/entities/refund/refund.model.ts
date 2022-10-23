import { ITransaction } from 'app/entities/transaction/transaction.model';
import { IUser } from 'app/entities/user/user.model';
import { RefundStatus } from 'app/entities/enumerations/refund-status.model';

export interface IRefund {
  id: number;
  amount?: number | null;
  reason?: string | null;
  orderCode?: number | null;
  status?: RefundStatus | null;
  transaction?: Pick<ITransaction, 'id' | 'code'> | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewRefund = Omit<IRefund, 'id'> & { id: null };
