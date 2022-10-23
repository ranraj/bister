import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IBookingOrder, NewBookingOrder } from '../booking-order.model';

export type PartialUpdateBookingOrder = Partial<IBookingOrder> & Pick<IBookingOrder, 'id'>;

type RestOf<T extends IBookingOrder | NewBookingOrder> = Omit<T, 'placedDate' | 'bookingExpieryDate'> & {
  placedDate?: string | null;
  bookingExpieryDate?: string | null;
};

export type RestBookingOrder = RestOf<IBookingOrder>;

export type NewRestBookingOrder = RestOf<NewBookingOrder>;

export type PartialUpdateRestBookingOrder = RestOf<PartialUpdateBookingOrder>;

export type EntityResponseType = HttpResponse<IBookingOrder>;
export type EntityArrayResponseType = HttpResponse<IBookingOrder[]>;

@Injectable({ providedIn: 'root' })
export class BookingOrderService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/booking-orders');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/booking-orders');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(bookingOrder: NewBookingOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bookingOrder);
    return this.http
      .post<RestBookingOrder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(bookingOrder: IBookingOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bookingOrder);
    return this.http
      .put<RestBookingOrder>(`${this.resourceUrl}/${this.getBookingOrderIdentifier(bookingOrder)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(bookingOrder: PartialUpdateBookingOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bookingOrder);
    return this.http
      .patch<RestBookingOrder>(`${this.resourceUrl}/${this.getBookingOrderIdentifier(bookingOrder)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestBookingOrder>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBookingOrder[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBookingOrder[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getBookingOrderIdentifier(bookingOrder: Pick<IBookingOrder, 'id'>): number {
    return bookingOrder.id;
  }

  compareBookingOrder(o1: Pick<IBookingOrder, 'id'> | null, o2: Pick<IBookingOrder, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookingOrderIdentifier(o1) === this.getBookingOrderIdentifier(o2) : o1 === o2;
  }

  addBookingOrderToCollectionIfMissing<Type extends Pick<IBookingOrder, 'id'>>(
    bookingOrderCollection: Type[],
    ...bookingOrdersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookingOrders: Type[] = bookingOrdersToCheck.filter(isPresent);
    if (bookingOrders.length > 0) {
      const bookingOrderCollectionIdentifiers = bookingOrderCollection.map(
        bookingOrderItem => this.getBookingOrderIdentifier(bookingOrderItem)!
      );
      const bookingOrdersToAdd = bookingOrders.filter(bookingOrderItem => {
        const bookingOrderIdentifier = this.getBookingOrderIdentifier(bookingOrderItem);
        if (bookingOrderCollectionIdentifiers.includes(bookingOrderIdentifier)) {
          return false;
        }
        bookingOrderCollectionIdentifiers.push(bookingOrderIdentifier);
        return true;
      });
      return [...bookingOrdersToAdd, ...bookingOrderCollection];
    }
    return bookingOrderCollection;
  }

  protected convertDateFromClient<T extends IBookingOrder | NewBookingOrder | PartialUpdateBookingOrder>(bookingOrder: T): RestOf<T> {
    return {
      ...bookingOrder,
      placedDate: bookingOrder.placedDate?.toJSON() ?? null,
      bookingExpieryDate: bookingOrder.bookingExpieryDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restBookingOrder: RestBookingOrder): IBookingOrder {
    return {
      ...restBookingOrder,
      placedDate: restBookingOrder.placedDate ? dayjs(restBookingOrder.placedDate) : undefined,
      bookingExpieryDate: restBookingOrder.bookingExpieryDate ? dayjs(restBookingOrder.bookingExpieryDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestBookingOrder>): HttpResponse<IBookingOrder> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestBookingOrder[]>): HttpResponse<IBookingOrder[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
