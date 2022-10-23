import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProductVariationAttributeTerm, NewProductVariationAttributeTerm } from '../product-variation-attribute-term.model';

export type PartialUpdateProductVariationAttributeTerm = Partial<IProductVariationAttributeTerm> &
  Pick<IProductVariationAttributeTerm, 'id'>;

export type EntityResponseType = HttpResponse<IProductVariationAttributeTerm>;
export type EntityArrayResponseType = HttpResponse<IProductVariationAttributeTerm[]>;

@Injectable({ providedIn: 'root' })
export class ProductVariationAttributeTermService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-variation-attribute-terms');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/product-variation-attribute-terms');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productVariationAttributeTerm: NewProductVariationAttributeTerm): Observable<EntityResponseType> {
    return this.http.post<IProductVariationAttributeTerm>(this.resourceUrl, productVariationAttributeTerm, { observe: 'response' });
  }

  update(productVariationAttributeTerm: IProductVariationAttributeTerm): Observable<EntityResponseType> {
    return this.http.put<IProductVariationAttributeTerm>(
      `${this.resourceUrl}/${this.getProductVariationAttributeTermIdentifier(productVariationAttributeTerm)}`,
      productVariationAttributeTerm,
      { observe: 'response' }
    );
  }

  partialUpdate(productVariationAttributeTerm: PartialUpdateProductVariationAttributeTerm): Observable<EntityResponseType> {
    return this.http.patch<IProductVariationAttributeTerm>(
      `${this.resourceUrl}/${this.getProductVariationAttributeTermIdentifier(productVariationAttributeTerm)}`,
      productVariationAttributeTerm,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductVariationAttributeTerm>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductVariationAttributeTerm[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductVariationAttributeTerm[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProductVariationAttributeTermIdentifier(productVariationAttributeTerm: Pick<IProductVariationAttributeTerm, 'id'>): number {
    return productVariationAttributeTerm.id;
  }

  compareProductVariationAttributeTerm(
    o1: Pick<IProductVariationAttributeTerm, 'id'> | null,
    o2: Pick<IProductVariationAttributeTerm, 'id'> | null
  ): boolean {
    return o1 && o2
      ? this.getProductVariationAttributeTermIdentifier(o1) === this.getProductVariationAttributeTermIdentifier(o2)
      : o1 === o2;
  }

  addProductVariationAttributeTermToCollectionIfMissing<Type extends Pick<IProductVariationAttributeTerm, 'id'>>(
    productVariationAttributeTermCollection: Type[],
    ...productVariationAttributeTermsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productVariationAttributeTerms: Type[] = productVariationAttributeTermsToCheck.filter(isPresent);
    if (productVariationAttributeTerms.length > 0) {
      const productVariationAttributeTermCollectionIdentifiers = productVariationAttributeTermCollection.map(
        productVariationAttributeTermItem => this.getProductVariationAttributeTermIdentifier(productVariationAttributeTermItem)!
      );
      const productVariationAttributeTermsToAdd = productVariationAttributeTerms.filter(productVariationAttributeTermItem => {
        const productVariationAttributeTermIdentifier = this.getProductVariationAttributeTermIdentifier(productVariationAttributeTermItem);
        if (productVariationAttributeTermCollectionIdentifiers.includes(productVariationAttributeTermIdentifier)) {
          return false;
        }
        productVariationAttributeTermCollectionIdentifiers.push(productVariationAttributeTermIdentifier);
        return true;
      });
      return [...productVariationAttributeTermsToAdd, ...productVariationAttributeTermCollection];
    }
    return productVariationAttributeTermCollection;
  }
}
