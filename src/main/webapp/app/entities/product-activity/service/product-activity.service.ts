import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProductActivity, NewProductActivity } from '../product-activity.model';

export type PartialUpdateProductActivity = Partial<IProductActivity> & Pick<IProductActivity, 'id'>;

export type EntityResponseType = HttpResponse<IProductActivity>;
export type EntityArrayResponseType = HttpResponse<IProductActivity[]>;

@Injectable({ providedIn: 'root' })
export class ProductActivityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-activities');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/product-activities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productActivity: NewProductActivity): Observable<EntityResponseType> {
    return this.http.post<IProductActivity>(this.resourceUrl, productActivity, { observe: 'response' });
  }

  update(productActivity: IProductActivity): Observable<EntityResponseType> {
    return this.http.put<IProductActivity>(`${this.resourceUrl}/${this.getProductActivityIdentifier(productActivity)}`, productActivity, {
      observe: 'response',
    });
  }

  partialUpdate(productActivity: PartialUpdateProductActivity): Observable<EntityResponseType> {
    return this.http.patch<IProductActivity>(`${this.resourceUrl}/${this.getProductActivityIdentifier(productActivity)}`, productActivity, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductActivity>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductActivity[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductActivity[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProductActivityIdentifier(productActivity: Pick<IProductActivity, 'id'>): number {
    return productActivity.id;
  }

  compareProductActivity(o1: Pick<IProductActivity, 'id'> | null, o2: Pick<IProductActivity, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductActivityIdentifier(o1) === this.getProductActivityIdentifier(o2) : o1 === o2;
  }

  addProductActivityToCollectionIfMissing<Type extends Pick<IProductActivity, 'id'>>(
    productActivityCollection: Type[],
    ...productActivitiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productActivities: Type[] = productActivitiesToCheck.filter(isPresent);
    if (productActivities.length > 0) {
      const productActivityCollectionIdentifiers = productActivityCollection.map(
        productActivityItem => this.getProductActivityIdentifier(productActivityItem)!
      );
      const productActivitiesToAdd = productActivities.filter(productActivityItem => {
        const productActivityIdentifier = this.getProductActivityIdentifier(productActivityItem);
        if (productActivityCollectionIdentifiers.includes(productActivityIdentifier)) {
          return false;
        }
        productActivityCollectionIdentifiers.push(productActivityIdentifier);
        return true;
      });
      return [...productActivitiesToAdd, ...productActivityCollection];
    }
    return productActivityCollection;
  }
}
