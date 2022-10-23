import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IRefund, NewRefund } from '../refund.model';

export type PartialUpdateRefund = Partial<IRefund> & Pick<IRefund, 'id'>;

export type EntityResponseType = HttpResponse<IRefund>;
export type EntityArrayResponseType = HttpResponse<IRefund[]>;

@Injectable({ providedIn: 'root' })
export class RefundService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/refunds');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/refunds');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(refund: NewRefund): Observable<EntityResponseType> {
    return this.http.post<IRefund>(this.resourceUrl, refund, { observe: 'response' });
  }

  update(refund: IRefund): Observable<EntityResponseType> {
    return this.http.put<IRefund>(`${this.resourceUrl}/${this.getRefundIdentifier(refund)}`, refund, { observe: 'response' });
  }

  partialUpdate(refund: PartialUpdateRefund): Observable<EntityResponseType> {
    return this.http.patch<IRefund>(`${this.resourceUrl}/${this.getRefundIdentifier(refund)}`, refund, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRefund>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRefund[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRefund[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getRefundIdentifier(refund: Pick<IRefund, 'id'>): number {
    return refund.id;
  }

  compareRefund(o1: Pick<IRefund, 'id'> | null, o2: Pick<IRefund, 'id'> | null): boolean {
    return o1 && o2 ? this.getRefundIdentifier(o1) === this.getRefundIdentifier(o2) : o1 === o2;
  }

  addRefundToCollectionIfMissing<Type extends Pick<IRefund, 'id'>>(
    refundCollection: Type[],
    ...refundsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const refunds: Type[] = refundsToCheck.filter(isPresent);
    if (refunds.length > 0) {
      const refundCollectionIdentifiers = refundCollection.map(refundItem => this.getRefundIdentifier(refundItem)!);
      const refundsToAdd = refunds.filter(refundItem => {
        const refundIdentifier = this.getRefundIdentifier(refundItem);
        if (refundCollectionIdentifiers.includes(refundIdentifier)) {
          return false;
        }
        refundCollectionIdentifiers.push(refundIdentifier);
        return true;
      });
      return [...refundsToAdd, ...refundCollection];
    }
    return refundCollection;
  }
}
