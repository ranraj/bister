import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProductSpecification, NewProductSpecification } from '../product-specification.model';

export type PartialUpdateProductSpecification = Partial<IProductSpecification> & Pick<IProductSpecification, 'id'>;

export type EntityResponseType = HttpResponse<IProductSpecification>;
export type EntityArrayResponseType = HttpResponse<IProductSpecification[]>;

@Injectable({ providedIn: 'root' })
export class ProductSpecificationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-specifications');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/product-specifications');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productSpecification: NewProductSpecification): Observable<EntityResponseType> {
    return this.http.post<IProductSpecification>(this.resourceUrl, productSpecification, { observe: 'response' });
  }

  update(productSpecification: IProductSpecification): Observable<EntityResponseType> {
    return this.http.put<IProductSpecification>(
      `${this.resourceUrl}/${this.getProductSpecificationIdentifier(productSpecification)}`,
      productSpecification,
      { observe: 'response' }
    );
  }

  partialUpdate(productSpecification: PartialUpdateProductSpecification): Observable<EntityResponseType> {
    return this.http.patch<IProductSpecification>(
      `${this.resourceUrl}/${this.getProductSpecificationIdentifier(productSpecification)}`,
      productSpecification,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductSpecification>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductSpecification[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductSpecification[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProductSpecificationIdentifier(productSpecification: Pick<IProductSpecification, 'id'>): number {
    return productSpecification.id;
  }

  compareProductSpecification(o1: Pick<IProductSpecification, 'id'> | null, o2: Pick<IProductSpecification, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductSpecificationIdentifier(o1) === this.getProductSpecificationIdentifier(o2) : o1 === o2;
  }

  addProductSpecificationToCollectionIfMissing<Type extends Pick<IProductSpecification, 'id'>>(
    productSpecificationCollection: Type[],
    ...productSpecificationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productSpecifications: Type[] = productSpecificationsToCheck.filter(isPresent);
    if (productSpecifications.length > 0) {
      const productSpecificationCollectionIdentifiers = productSpecificationCollection.map(
        productSpecificationItem => this.getProductSpecificationIdentifier(productSpecificationItem)!
      );
      const productSpecificationsToAdd = productSpecifications.filter(productSpecificationItem => {
        const productSpecificationIdentifier = this.getProductSpecificationIdentifier(productSpecificationItem);
        if (productSpecificationCollectionIdentifiers.includes(productSpecificationIdentifier)) {
          return false;
        }
        productSpecificationCollectionIdentifiers.push(productSpecificationIdentifier);
        return true;
      });
      return [...productSpecificationsToAdd, ...productSpecificationCollection];
    }
    return productSpecificationCollection;
  }
}
