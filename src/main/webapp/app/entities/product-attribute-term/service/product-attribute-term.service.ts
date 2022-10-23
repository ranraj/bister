import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProductAttributeTerm, NewProductAttributeTerm } from '../product-attribute-term.model';

export type PartialUpdateProductAttributeTerm = Partial<IProductAttributeTerm> & Pick<IProductAttributeTerm, 'id'>;

export type EntityResponseType = HttpResponse<IProductAttributeTerm>;
export type EntityArrayResponseType = HttpResponse<IProductAttributeTerm[]>;

@Injectable({ providedIn: 'root' })
export class ProductAttributeTermService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-attribute-terms');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/product-attribute-terms');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productAttributeTerm: NewProductAttributeTerm): Observable<EntityResponseType> {
    return this.http.post<IProductAttributeTerm>(this.resourceUrl, productAttributeTerm, { observe: 'response' });
  }

  update(productAttributeTerm: IProductAttributeTerm): Observable<EntityResponseType> {
    return this.http.put<IProductAttributeTerm>(
      `${this.resourceUrl}/${this.getProductAttributeTermIdentifier(productAttributeTerm)}`,
      productAttributeTerm,
      { observe: 'response' }
    );
  }

  partialUpdate(productAttributeTerm: PartialUpdateProductAttributeTerm): Observable<EntityResponseType> {
    return this.http.patch<IProductAttributeTerm>(
      `${this.resourceUrl}/${this.getProductAttributeTermIdentifier(productAttributeTerm)}`,
      productAttributeTerm,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductAttributeTerm>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductAttributeTerm[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductAttributeTerm[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProductAttributeTermIdentifier(productAttributeTerm: Pick<IProductAttributeTerm, 'id'>): number {
    return productAttributeTerm.id;
  }

  compareProductAttributeTerm(o1: Pick<IProductAttributeTerm, 'id'> | null, o2: Pick<IProductAttributeTerm, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductAttributeTermIdentifier(o1) === this.getProductAttributeTermIdentifier(o2) : o1 === o2;
  }

  addProductAttributeTermToCollectionIfMissing<Type extends Pick<IProductAttributeTerm, 'id'>>(
    productAttributeTermCollection: Type[],
    ...productAttributeTermsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productAttributeTerms: Type[] = productAttributeTermsToCheck.filter(isPresent);
    if (productAttributeTerms.length > 0) {
      const productAttributeTermCollectionIdentifiers = productAttributeTermCollection.map(
        productAttributeTermItem => this.getProductAttributeTermIdentifier(productAttributeTermItem)!
      );
      const productAttributeTermsToAdd = productAttributeTerms.filter(productAttributeTermItem => {
        const productAttributeTermIdentifier = this.getProductAttributeTermIdentifier(productAttributeTermItem);
        if (productAttributeTermCollectionIdentifiers.includes(productAttributeTermIdentifier)) {
          return false;
        }
        productAttributeTermCollectionIdentifiers.push(productAttributeTermIdentifier);
        return true;
      });
      return [...productAttributeTermsToAdd, ...productAttributeTermCollection];
    }
    return productAttributeTermCollection;
  }
}
