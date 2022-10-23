import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPaymentSchedule, NewPaymentSchedule } from '../payment-schedule.model';

export type PartialUpdatePaymentSchedule = Partial<IPaymentSchedule> & Pick<IPaymentSchedule, 'id'>;

type RestOf<T extends IPaymentSchedule | NewPaymentSchedule> = Omit<T, 'dueDate'> & {
  dueDate?: string | null;
};

export type RestPaymentSchedule = RestOf<IPaymentSchedule>;

export type NewRestPaymentSchedule = RestOf<NewPaymentSchedule>;

export type PartialUpdateRestPaymentSchedule = RestOf<PartialUpdatePaymentSchedule>;

export type EntityResponseType = HttpResponse<IPaymentSchedule>;
export type EntityArrayResponseType = HttpResponse<IPaymentSchedule[]>;

@Injectable({ providedIn: 'root' })
export class PaymentScheduleService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/payment-schedules');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/payment-schedules');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(paymentSchedule: NewPaymentSchedule): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paymentSchedule);
    return this.http
      .post<RestPaymentSchedule>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(paymentSchedule: IPaymentSchedule): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paymentSchedule);
    return this.http
      .put<RestPaymentSchedule>(`${this.resourceUrl}/${this.getPaymentScheduleIdentifier(paymentSchedule)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(paymentSchedule: PartialUpdatePaymentSchedule): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paymentSchedule);
    return this.http
      .patch<RestPaymentSchedule>(`${this.resourceUrl}/${this.getPaymentScheduleIdentifier(paymentSchedule)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPaymentSchedule>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPaymentSchedule[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPaymentSchedule[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getPaymentScheduleIdentifier(paymentSchedule: Pick<IPaymentSchedule, 'id'>): number {
    return paymentSchedule.id;
  }

  comparePaymentSchedule(o1: Pick<IPaymentSchedule, 'id'> | null, o2: Pick<IPaymentSchedule, 'id'> | null): boolean {
    return o1 && o2 ? this.getPaymentScheduleIdentifier(o1) === this.getPaymentScheduleIdentifier(o2) : o1 === o2;
  }

  addPaymentScheduleToCollectionIfMissing<Type extends Pick<IPaymentSchedule, 'id'>>(
    paymentScheduleCollection: Type[],
    ...paymentSchedulesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const paymentSchedules: Type[] = paymentSchedulesToCheck.filter(isPresent);
    if (paymentSchedules.length > 0) {
      const paymentScheduleCollectionIdentifiers = paymentScheduleCollection.map(
        paymentScheduleItem => this.getPaymentScheduleIdentifier(paymentScheduleItem)!
      );
      const paymentSchedulesToAdd = paymentSchedules.filter(paymentScheduleItem => {
        const paymentScheduleIdentifier = this.getPaymentScheduleIdentifier(paymentScheduleItem);
        if (paymentScheduleCollectionIdentifiers.includes(paymentScheduleIdentifier)) {
          return false;
        }
        paymentScheduleCollectionIdentifiers.push(paymentScheduleIdentifier);
        return true;
      });
      return [...paymentSchedulesToAdd, ...paymentScheduleCollection];
    }
    return paymentScheduleCollection;
  }

  protected convertDateFromClient<T extends IPaymentSchedule | NewPaymentSchedule | PartialUpdatePaymentSchedule>(
    paymentSchedule: T
  ): RestOf<T> {
    return {
      ...paymentSchedule,
      dueDate: paymentSchedule.dueDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPaymentSchedule: RestPaymentSchedule): IPaymentSchedule {
    return {
      ...restPaymentSchedule,
      dueDate: restPaymentSchedule.dueDate ? dayjs(restPaymentSchedule.dueDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPaymentSchedule>): HttpResponse<IPaymentSchedule> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPaymentSchedule[]>): HttpResponse<IPaymentSchedule[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
