import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';

import { ITransaction, NewTransaction } from './transaction.model';

export const sampleWithRequiredData: ITransaction = {
  id: 73739,
  code: 'wireless',
  amount: 52022,
  status: TransactionStatus['COMPLETE'],
};

export const sampleWithPartialData: ITransaction = {
  id: 86831,
  code: 'set Small Interactions',
  amount: 29737,
  status: TransactionStatus['REJECTED'],
};

export const sampleWithFullData: ITransaction = {
  id: 7950,
  code: 'invoice',
  amount: 90321,
  status: TransactionStatus['PENDING'],
};

export const sampleWithNewData: NewTransaction = {
  code: 'Computers clear-thinking Movies',
  amount: 90765,
  status: TransactionStatus['REJECTED'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
