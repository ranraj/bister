import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IEnquiryResponse, NewEnquiryResponse } from '../enquiry-response.model';

export type PartialUpdateEnquiryResponse = Partial<IEnquiryResponse> & Pick<IEnquiryResponse, 'id'>;

export type EntityResponseType = HttpResponse<IEnquiryResponse>;
export type EntityArrayResponseType = HttpResponse<IEnquiryResponse[]>;

@Injectable({ providedIn: 'root' })
export class EnquiryResponseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/enquiry-responses');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/enquiry-responses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(enquiryResponse: NewEnquiryResponse): Observable<EntityResponseType> {
    return this.http.post<IEnquiryResponse>(this.resourceUrl, enquiryResponse, { observe: 'response' });
  }

  update(enquiryResponse: IEnquiryResponse): Observable<EntityResponseType> {
    return this.http.put<IEnquiryResponse>(`${this.resourceUrl}/${this.getEnquiryResponseIdentifier(enquiryResponse)}`, enquiryResponse, {
      observe: 'response',
    });
  }

  partialUpdate(enquiryResponse: PartialUpdateEnquiryResponse): Observable<EntityResponseType> {
    return this.http.patch<IEnquiryResponse>(`${this.resourceUrl}/${this.getEnquiryResponseIdentifier(enquiryResponse)}`, enquiryResponse, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEnquiryResponse>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEnquiryResponse[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEnquiryResponse[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getEnquiryResponseIdentifier(enquiryResponse: Pick<IEnquiryResponse, 'id'>): number {
    return enquiryResponse.id;
  }

  compareEnquiryResponse(o1: Pick<IEnquiryResponse, 'id'> | null, o2: Pick<IEnquiryResponse, 'id'> | null): boolean {
    return o1 && o2 ? this.getEnquiryResponseIdentifier(o1) === this.getEnquiryResponseIdentifier(o2) : o1 === o2;
  }

  addEnquiryResponseToCollectionIfMissing<Type extends Pick<IEnquiryResponse, 'id'>>(
    enquiryResponseCollection: Type[],
    ...enquiryResponsesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const enquiryResponses: Type[] = enquiryResponsesToCheck.filter(isPresent);
    if (enquiryResponses.length > 0) {
      const enquiryResponseCollectionIdentifiers = enquiryResponseCollection.map(
        enquiryResponseItem => this.getEnquiryResponseIdentifier(enquiryResponseItem)!
      );
      const enquiryResponsesToAdd = enquiryResponses.filter(enquiryResponseItem => {
        const enquiryResponseIdentifier = this.getEnquiryResponseIdentifier(enquiryResponseItem);
        if (enquiryResponseCollectionIdentifiers.includes(enquiryResponseIdentifier)) {
          return false;
        }
        enquiryResponseCollectionIdentifiers.push(enquiryResponseIdentifier);
        return true;
      });
      return [...enquiryResponsesToAdd, ...enquiryResponseCollection];
    }
    return enquiryResponseCollection;
  }
}
