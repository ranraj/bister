import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPromotion, NewPromotion } from '../promotion.model';

export type PartialUpdatePromotion = Partial<IPromotion> & Pick<IPromotion, 'id'>;

type RestOf<T extends IPromotion | NewPromotion> = Omit<T, 'createdAt' | 'sendAt'> & {
  createdAt?: string | null;
  sendAt?: string | null;
};

export type RestPromotion = RestOf<IPromotion>;

export type NewRestPromotion = RestOf<NewPromotion>;

export type PartialUpdateRestPromotion = RestOf<PartialUpdatePromotion>;

export type EntityResponseType = HttpResponse<IPromotion>;
export type EntityArrayResponseType = HttpResponse<IPromotion[]>;

@Injectable({ providedIn: 'root' })
export class PromotionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/promotions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/promotions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(promotion: NewPromotion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(promotion);
    return this.http
      .post<RestPromotion>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(promotion: IPromotion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(promotion);
    return this.http
      .put<RestPromotion>(`${this.resourceUrl}/${this.getPromotionIdentifier(promotion)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(promotion: PartialUpdatePromotion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(promotion);
    return this.http
      .patch<RestPromotion>(`${this.resourceUrl}/${this.getPromotionIdentifier(promotion)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPromotion>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPromotion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPromotion[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getPromotionIdentifier(promotion: Pick<IPromotion, 'id'>): number {
    return promotion.id;
  }

  comparePromotion(o1: Pick<IPromotion, 'id'> | null, o2: Pick<IPromotion, 'id'> | null): boolean {
    return o1 && o2 ? this.getPromotionIdentifier(o1) === this.getPromotionIdentifier(o2) : o1 === o2;
  }

  addPromotionToCollectionIfMissing<Type extends Pick<IPromotion, 'id'>>(
    promotionCollection: Type[],
    ...promotionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const promotions: Type[] = promotionsToCheck.filter(isPresent);
    if (promotions.length > 0) {
      const promotionCollectionIdentifiers = promotionCollection.map(promotionItem => this.getPromotionIdentifier(promotionItem)!);
      const promotionsToAdd = promotions.filter(promotionItem => {
        const promotionIdentifier = this.getPromotionIdentifier(promotionItem);
        if (promotionCollectionIdentifiers.includes(promotionIdentifier)) {
          return false;
        }
        promotionCollectionIdentifiers.push(promotionIdentifier);
        return true;
      });
      return [...promotionsToAdd, ...promotionCollection];
    }
    return promotionCollection;
  }

  protected convertDateFromClient<T extends IPromotion | NewPromotion | PartialUpdatePromotion>(promotion: T): RestOf<T> {
    return {
      ...promotion,
      createdAt: promotion.createdAt?.toJSON() ?? null,
      sendAt: promotion.sendAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPromotion: RestPromotion): IPromotion {
    return {
      ...restPromotion,
      createdAt: restPromotion.createdAt ? dayjs(restPromotion.createdAt) : undefined,
      sendAt: restPromotion.sendAt ? dayjs(restPromotion.sendAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPromotion>): HttpResponse<IPromotion> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPromotion[]>): HttpResponse<IPromotion[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
