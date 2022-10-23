import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITaxRate, NewTaxRate } from '../tax-rate.model';

export type PartialUpdateTaxRate = Partial<ITaxRate> & Pick<ITaxRate, 'id'>;

export type EntityResponseType = HttpResponse<ITaxRate>;
export type EntityArrayResponseType = HttpResponse<ITaxRate[]>;

@Injectable({ providedIn: 'root' })
export class TaxRateService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tax-rates');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/tax-rates');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(taxRate: NewTaxRate): Observable<EntityResponseType> {
    return this.http.post<ITaxRate>(this.resourceUrl, taxRate, { observe: 'response' });
  }

  update(taxRate: ITaxRate): Observable<EntityResponseType> {
    return this.http.put<ITaxRate>(`${this.resourceUrl}/${this.getTaxRateIdentifier(taxRate)}`, taxRate, { observe: 'response' });
  }

  partialUpdate(taxRate: PartialUpdateTaxRate): Observable<EntityResponseType> {
    return this.http.patch<ITaxRate>(`${this.resourceUrl}/${this.getTaxRateIdentifier(taxRate)}`, taxRate, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITaxRate>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITaxRate[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITaxRate[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getTaxRateIdentifier(taxRate: Pick<ITaxRate, 'id'>): number {
    return taxRate.id;
  }

  compareTaxRate(o1: Pick<ITaxRate, 'id'> | null, o2: Pick<ITaxRate, 'id'> | null): boolean {
    return o1 && o2 ? this.getTaxRateIdentifier(o1) === this.getTaxRateIdentifier(o2) : o1 === o2;
  }

  addTaxRateToCollectionIfMissing<Type extends Pick<ITaxRate, 'id'>>(
    taxRateCollection: Type[],
    ...taxRatesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const taxRates: Type[] = taxRatesToCheck.filter(isPresent);
    if (taxRates.length > 0) {
      const taxRateCollectionIdentifiers = taxRateCollection.map(taxRateItem => this.getTaxRateIdentifier(taxRateItem)!);
      const taxRatesToAdd = taxRates.filter(taxRateItem => {
        const taxRateIdentifier = this.getTaxRateIdentifier(taxRateItem);
        if (taxRateCollectionIdentifiers.includes(taxRateIdentifier)) {
          return false;
        }
        taxRateCollectionIdentifiers.push(taxRateIdentifier);
        return true;
      });
      return [...taxRatesToAdd, ...taxRateCollection];
    }
    return taxRateCollection;
  }
}
