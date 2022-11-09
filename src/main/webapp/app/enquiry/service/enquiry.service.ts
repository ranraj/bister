import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IEnquiry, NewEnquiry } from '../enquiry.model';

export type PartialUpdateEnquiry = Partial<IEnquiry> & Pick<IEnquiry, 'id'>;

type RestOf<T extends IEnquiry | NewEnquiry> = Omit<T, 'raisedDate' | 'lastResponseDate'> & {
  raisedDate?: string | null;
  lastResponseDate?: string | null;
};

export type RestEnquiry = RestOf<IEnquiry>;

export type NewRestEnquiry = RestOf<NewEnquiry>;

export type PartialUpdateRestEnquiry = RestOf<PartialUpdateEnquiry>;

export type EntityResponseType = HttpResponse<IEnquiry>;
export type EntityArrayResponseType = HttpResponse<IEnquiry[]>;

@Injectable({ providedIn: 'root' })
export class EnquiryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/enquiries');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/enquiries');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(enquiry: NewEnquiry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(enquiry);
    return this.http
      .post<RestEnquiry>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(enquiry: IEnquiry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(enquiry);
    return this.http
      .put<RestEnquiry>(`${this.resourceUrl}/${this.getEnquiryIdentifier(enquiry)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(enquiry: PartialUpdateEnquiry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(enquiry);
    return this.http
      .patch<RestEnquiry>(`${this.resourceUrl}/${this.getEnquiryIdentifier(enquiry)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEnquiry>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEnquiry[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEnquiry[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getEnquiryIdentifier(enquiry: Pick<IEnquiry, 'id'>): number {
    return enquiry.id;
  }

  compareEnquiry(o1: Pick<IEnquiry, 'id'> | null, o2: Pick<IEnquiry, 'id'> | null): boolean {
    return o1 && o2 ? this.getEnquiryIdentifier(o1) === this.getEnquiryIdentifier(o2) : o1 === o2;
  }

  addEnquiryToCollectionIfMissing<Type extends Pick<IEnquiry, 'id'>>(
    enquiryCollection: Type[],
    ...enquiriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const enquiries: Type[] = enquiriesToCheck.filter(isPresent);
    if (enquiries.length > 0) {
      const enquiryCollectionIdentifiers = enquiryCollection.map(enquiryItem => this.getEnquiryIdentifier(enquiryItem)!);
      const enquiriesToAdd = enquiries.filter(enquiryItem => {
        const enquiryIdentifier = this.getEnquiryIdentifier(enquiryItem);
        if (enquiryCollectionIdentifiers.includes(enquiryIdentifier)) {
          return false;
        }
        enquiryCollectionIdentifiers.push(enquiryIdentifier);
        return true;
      });
      return [...enquiriesToAdd, ...enquiryCollection];
    }
    return enquiryCollection;
  }

  protected convertDateFromClient<T extends IEnquiry | NewEnquiry | PartialUpdateEnquiry>(enquiry: T): RestOf<T> {
    return {
      ...enquiry,
      raisedDate: enquiry.raisedDate?.toJSON() ?? null,
      lastResponseDate: enquiry.lastResponseDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEnquiry: RestEnquiry): IEnquiry {
    return {
      ...restEnquiry,
      raisedDate: restEnquiry.raisedDate ? dayjs(restEnquiry.raisedDate) : undefined,
      lastResponseDate: restEnquiry.lastResponseDate ? dayjs(restEnquiry.lastResponseDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEnquiry>): HttpResponse<IEnquiry> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEnquiry[]>): HttpResponse<IEnquiry[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
