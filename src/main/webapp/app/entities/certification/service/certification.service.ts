import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ICertification, NewCertification } from '../certification.model';

export type PartialUpdateCertification = Partial<ICertification> & Pick<ICertification, 'id'>;

type RestOf<T extends ICertification | NewCertification> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestCertification = RestOf<ICertification>;

export type NewRestCertification = RestOf<NewCertification>;

export type PartialUpdateRestCertification = RestOf<PartialUpdateCertification>;

export type EntityResponseType = HttpResponse<ICertification>;
export type EntityArrayResponseType = HttpResponse<ICertification[]>;

@Injectable({ providedIn: 'root' })
export class CertificationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/certifications');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/certifications');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(certification: NewCertification): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(certification);
    return this.http
      .post<RestCertification>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(certification: ICertification): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(certification);
    return this.http
      .put<RestCertification>(`${this.resourceUrl}/${this.getCertificationIdentifier(certification)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(certification: PartialUpdateCertification): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(certification);
    return this.http
      .patch<RestCertification>(`${this.resourceUrl}/${this.getCertificationIdentifier(certification)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCertification>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCertification[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCertification[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getCertificationIdentifier(certification: Pick<ICertification, 'id'>): number {
    return certification.id;
  }

  compareCertification(o1: Pick<ICertification, 'id'> | null, o2: Pick<ICertification, 'id'> | null): boolean {
    return o1 && o2 ? this.getCertificationIdentifier(o1) === this.getCertificationIdentifier(o2) : o1 === o2;
  }

  addCertificationToCollectionIfMissing<Type extends Pick<ICertification, 'id'>>(
    certificationCollection: Type[],
    ...certificationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const certifications: Type[] = certificationsToCheck.filter(isPresent);
    if (certifications.length > 0) {
      const certificationCollectionIdentifiers = certificationCollection.map(
        certificationItem => this.getCertificationIdentifier(certificationItem)!
      );
      const certificationsToAdd = certifications.filter(certificationItem => {
        const certificationIdentifier = this.getCertificationIdentifier(certificationItem);
        if (certificationCollectionIdentifiers.includes(certificationIdentifier)) {
          return false;
        }
        certificationCollectionIdentifiers.push(certificationIdentifier);
        return true;
      });
      return [...certificationsToAdd, ...certificationCollection];
    }
    return certificationCollection;
  }

  protected convertDateFromClient<T extends ICertification | NewCertification | PartialUpdateCertification>(certification: T): RestOf<T> {
    return {
      ...certification,
      createdAt: certification.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCertification: RestCertification): ICertification {
    return {
      ...restCertification,
      createdAt: restCertification.createdAt ? dayjs(restCertification.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCertification>): HttpResponse<ICertification> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCertification[]>): HttpResponse<ICertification[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
