import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITaxClass, NewTaxClass } from '../tax-class.model';

export type PartialUpdateTaxClass = Partial<ITaxClass> & Pick<ITaxClass, 'id'>;

export type EntityResponseType = HttpResponse<ITaxClass>;
export type EntityArrayResponseType = HttpResponse<ITaxClass[]>;

@Injectable({ providedIn: 'root' })
export class TaxClassService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tax-classes');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/tax-classes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(taxClass: NewTaxClass): Observable<EntityResponseType> {
    return this.http.post<ITaxClass>(this.resourceUrl, taxClass, { observe: 'response' });
  }

  update(taxClass: ITaxClass): Observable<EntityResponseType> {
    return this.http.put<ITaxClass>(`${this.resourceUrl}/${this.getTaxClassIdentifier(taxClass)}`, taxClass, { observe: 'response' });
  }

  partialUpdate(taxClass: PartialUpdateTaxClass): Observable<EntityResponseType> {
    return this.http.patch<ITaxClass>(`${this.resourceUrl}/${this.getTaxClassIdentifier(taxClass)}`, taxClass, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITaxClass>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITaxClass[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITaxClass[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getTaxClassIdentifier(taxClass: Pick<ITaxClass, 'id'>): number {
    return taxClass.id;
  }

  compareTaxClass(o1: Pick<ITaxClass, 'id'> | null, o2: Pick<ITaxClass, 'id'> | null): boolean {
    return o1 && o2 ? this.getTaxClassIdentifier(o1) === this.getTaxClassIdentifier(o2) : o1 === o2;
  }

  addTaxClassToCollectionIfMissing<Type extends Pick<ITaxClass, 'id'>>(
    taxClassCollection: Type[],
    ...taxClassesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const taxClasses: Type[] = taxClassesToCheck.filter(isPresent);
    if (taxClasses.length > 0) {
      const taxClassCollectionIdentifiers = taxClassCollection.map(taxClassItem => this.getTaxClassIdentifier(taxClassItem)!);
      const taxClassesToAdd = taxClasses.filter(taxClassItem => {
        const taxClassIdentifier = this.getTaxClassIdentifier(taxClassItem);
        if (taxClassCollectionIdentifiers.includes(taxClassIdentifier)) {
          return false;
        }
        taxClassCollectionIdentifiers.push(taxClassIdentifier);
        return true;
      });
      return [...taxClassesToAdd, ...taxClassCollection];
    }
    return taxClassCollection;
  }
}
