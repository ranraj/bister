import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProductAttribute, NewProductAttribute } from '../product-attribute.model';

export type PartialUpdateProductAttribute = Partial<IProductAttribute> & Pick<IProductAttribute, 'id'>;

export type EntityResponseType = HttpResponse<IProductAttribute>;
export type EntityArrayResponseType = HttpResponse<IProductAttribute[]>;

@Injectable({ providedIn: 'root' })
export class ProductAttributeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-attributes');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/product-attributes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productAttribute: NewProductAttribute): Observable<EntityResponseType> {
    return this.http.post<IProductAttribute>(this.resourceUrl, productAttribute, { observe: 'response' });
  }

  update(productAttribute: IProductAttribute): Observable<EntityResponseType> {
    return this.http.put<IProductAttribute>(
      `${this.resourceUrl}/${this.getProductAttributeIdentifier(productAttribute)}`,
      productAttribute,
      { observe: 'response' }
    );
  }

  partialUpdate(productAttribute: PartialUpdateProductAttribute): Observable<EntityResponseType> {
    return this.http.patch<IProductAttribute>(
      `${this.resourceUrl}/${this.getProductAttributeIdentifier(productAttribute)}`,
      productAttribute,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductAttribute>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductAttribute[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductAttribute[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProductAttributeIdentifier(productAttribute: Pick<IProductAttribute, 'id'>): number {
    return productAttribute.id;
  }

  compareProductAttribute(o1: Pick<IProductAttribute, 'id'> | null, o2: Pick<IProductAttribute, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductAttributeIdentifier(o1) === this.getProductAttributeIdentifier(o2) : o1 === o2;
  }

  addProductAttributeToCollectionIfMissing<Type extends Pick<IProductAttribute, 'id'>>(
    productAttributeCollection: Type[],
    ...productAttributesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productAttributes: Type[] = productAttributesToCheck.filter(isPresent);
    if (productAttributes.length > 0) {
      const productAttributeCollectionIdentifiers = productAttributeCollection.map(
        productAttributeItem => this.getProductAttributeIdentifier(productAttributeItem)!
      );
      const productAttributesToAdd = productAttributes.filter(productAttributeItem => {
        const productAttributeIdentifier = this.getProductAttributeIdentifier(productAttributeItem);
        if (productAttributeCollectionIdentifiers.includes(productAttributeIdentifier)) {
          return false;
        }
        productAttributeCollectionIdentifiers.push(productAttributeIdentifier);
        return true;
      });
      return [...productAttributesToAdd, ...productAttributeCollection];
    }
    return productAttributeCollection;
  }
}
