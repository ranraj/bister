import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProductReview, NewProductReview } from '../product-review.model';

export type PartialUpdateProductReview = Partial<IProductReview> & Pick<IProductReview, 'id'>;

export type EntityResponseType = HttpResponse<IProductReview>;
export type EntityArrayResponseType = HttpResponse<IProductReview[]>;

@Injectable({ providedIn: 'root' })
export class ProductReviewService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-reviews');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/product-reviews');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productReview: NewProductReview): Observable<EntityResponseType> {
    return this.http.post<IProductReview>(this.resourceUrl, productReview, { observe: 'response' });
  }

  update(productReview: IProductReview): Observable<EntityResponseType> {
    return this.http.put<IProductReview>(`${this.resourceUrl}/${this.getProductReviewIdentifier(productReview)}`, productReview, {
      observe: 'response',
    });
  }

  partialUpdate(productReview: PartialUpdateProductReview): Observable<EntityResponseType> {
    return this.http.patch<IProductReview>(`${this.resourceUrl}/${this.getProductReviewIdentifier(productReview)}`, productReview, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductReview>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductReview[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductReview[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProductReviewIdentifier(productReview: Pick<IProductReview, 'id'>): number {
    return productReview.id;
  }

  compareProductReview(o1: Pick<IProductReview, 'id'> | null, o2: Pick<IProductReview, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductReviewIdentifier(o1) === this.getProductReviewIdentifier(o2) : o1 === o2;
  }

  addProductReviewToCollectionIfMissing<Type extends Pick<IProductReview, 'id'>>(
    productReviewCollection: Type[],
    ...productReviewsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productReviews: Type[] = productReviewsToCheck.filter(isPresent);
    if (productReviews.length > 0) {
      const productReviewCollectionIdentifiers = productReviewCollection.map(
        productReviewItem => this.getProductReviewIdentifier(productReviewItem)!
      );
      const productReviewsToAdd = productReviews.filter(productReviewItem => {
        const productReviewIdentifier = this.getProductReviewIdentifier(productReviewItem);
        if (productReviewCollectionIdentifiers.includes(productReviewIdentifier)) {
          return false;
        }
        productReviewCollectionIdentifiers.push(productReviewIdentifier);
        return true;
      });
      return [...productReviewsToAdd, ...productReviewCollection];
    }
    return productReviewCollection;
  }
}
