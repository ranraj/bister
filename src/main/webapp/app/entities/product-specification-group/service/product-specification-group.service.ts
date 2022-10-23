import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProductSpecificationGroup, NewProductSpecificationGroup } from '../product-specification-group.model';

export type PartialUpdateProductSpecificationGroup = Partial<IProductSpecificationGroup> & Pick<IProductSpecificationGroup, 'id'>;

export type EntityResponseType = HttpResponse<IProductSpecificationGroup>;
export type EntityArrayResponseType = HttpResponse<IProductSpecificationGroup[]>;

@Injectable({ providedIn: 'root' })
export class ProductSpecificationGroupService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-specification-groups');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/product-specification-groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productSpecificationGroup: NewProductSpecificationGroup): Observable<EntityResponseType> {
    return this.http.post<IProductSpecificationGroup>(this.resourceUrl, productSpecificationGroup, { observe: 'response' });
  }

  update(productSpecificationGroup: IProductSpecificationGroup): Observable<EntityResponseType> {
    return this.http.put<IProductSpecificationGroup>(
      `${this.resourceUrl}/${this.getProductSpecificationGroupIdentifier(productSpecificationGroup)}`,
      productSpecificationGroup,
      { observe: 'response' }
    );
  }

  partialUpdate(productSpecificationGroup: PartialUpdateProductSpecificationGroup): Observable<EntityResponseType> {
    return this.http.patch<IProductSpecificationGroup>(
      `${this.resourceUrl}/${this.getProductSpecificationGroupIdentifier(productSpecificationGroup)}`,
      productSpecificationGroup,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductSpecificationGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductSpecificationGroup[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductSpecificationGroup[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProductSpecificationGroupIdentifier(productSpecificationGroup: Pick<IProductSpecificationGroup, 'id'>): number {
    return productSpecificationGroup.id;
  }

  compareProductSpecificationGroup(
    o1: Pick<IProductSpecificationGroup, 'id'> | null,
    o2: Pick<IProductSpecificationGroup, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getProductSpecificationGroupIdentifier(o1) === this.getProductSpecificationGroupIdentifier(o2) : o1 === o2;
  }

  addProductSpecificationGroupToCollectionIfMissing<Type extends Pick<IProductSpecificationGroup, 'id'>>(
    productSpecificationGroupCollection: Type[],
    ...productSpecificationGroupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productSpecificationGroups: Type[] = productSpecificationGroupsToCheck.filter(isPresent);
    if (productSpecificationGroups.length > 0) {
      const productSpecificationGroupCollectionIdentifiers = productSpecificationGroupCollection.map(
        productSpecificationGroupItem => this.getProductSpecificationGroupIdentifier(productSpecificationGroupItem)!
      );
      const productSpecificationGroupsToAdd = productSpecificationGroups.filter(productSpecificationGroupItem => {
        const productSpecificationGroupIdentifier = this.getProductSpecificationGroupIdentifier(productSpecificationGroupItem);
        if (productSpecificationGroupCollectionIdentifiers.includes(productSpecificationGroupIdentifier)) {
          return false;
        }
        productSpecificationGroupCollectionIdentifiers.push(productSpecificationGroupIdentifier);
        return true;
      });
      return [...productSpecificationGroupsToAdd, ...productSpecificationGroupCollection];
    }
    return productSpecificationGroupCollection;
  }
}
