import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';

export interface ITransaction {
  id: number;
  code?: string | null;
  amount?: number | null;
  status?: TransactionStatus | null;
  purchaseOrder?: Pick<IPurchaseOrder, 'id'> | null;
}

export type NewTransaction = Omit<ITransaction, 'id'> & { id: null };
