import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITransaction, NewTransaction } from '../transaction.model';

export type PartialUpdateTransaction = Partial<ITransaction> & Pick<ITransaction, 'id'>;

export type EntityResponseType = HttpResponse<ITransaction>;
export type EntityArrayResponseType = HttpResponse<ITransaction[]>;

@Injectable({ providedIn: 'root' })
export class TransactionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/transactions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/transactions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(transaction: NewTransaction): Observable<EntityResponseType> {
    return this.http.post<ITransaction>(this.resourceUrl, transaction, { observe: 'response' });
  }

  update(transaction: ITransaction): Observable<EntityResponseType> {
    return this.http.put<ITransaction>(`${this.resourceUrl}/${this.getTransactionIdentifier(transaction)}`, transaction, {
      observe: 'response',
    });
  }

  partialUpdate(transaction: PartialUpdateTransaction): Observable<EntityResponseType> {
    return this.http.patch<ITransaction>(`${this.resourceUrl}/${this.getTransactionIdentifier(transaction)}`, transaction, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITransaction[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITransaction[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getTransactionIdentifier(transaction: Pick<ITransaction, 'id'>): number {
    return transaction.id;
  }

  compareTransaction(o1: Pick<ITransaction, 'id'> | null, o2: Pick<ITransaction, 'id'> | null): boolean {
    return o1 && o2 ? this.getTransactionIdentifier(o1) === this.getTransactionIdentifier(o2) : o1 === o2;
  }

  addTransactionToCollectionIfMissing<Type extends Pick<ITransaction, 'id'>>(
    transactionCollection: Type[],
    ...transactionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const transactions: Type[] = transactionsToCheck.filter(isPresent);
    if (transactions.length > 0) {
      const transactionCollectionIdentifiers = transactionCollection.map(
        transactionItem => this.getTransactionIdentifier(transactionItem)!
      );
      const transactionsToAdd = transactions.filter(transactionItem => {
        const transactionIdentifier = this.getTransactionIdentifier(transactionItem);
        if (transactionCollectionIdentifiers.includes(transactionIdentifier)) {
          return false;
        }
        transactionCollectionIdentifiers.push(transactionIdentifier);
        return true;
      });
      return [...transactionsToAdd, ...transactionCollection];
    }
    return transactionCollection;
  }
}
