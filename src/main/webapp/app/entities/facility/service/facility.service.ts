import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IFacility, NewFacility } from '../facility.model';

export type PartialUpdateFacility = Partial<IFacility> & Pick<IFacility, 'id'>;

export type EntityResponseType = HttpResponse<IFacility>;
export type EntityArrayResponseType = HttpResponse<IFacility[]>;

@Injectable({ providedIn: 'root' })
export class FacilityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/facilities');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/facilities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(facility: NewFacility): Observable<EntityResponseType> {
    return this.http.post<IFacility>(this.resourceUrl, facility, { observe: 'response' });
  }

  update(facility: IFacility): Observable<EntityResponseType> {
    return this.http.put<IFacility>(`${this.resourceUrl}/${this.getFacilityIdentifier(facility)}`, facility, { observe: 'response' });
  }

  partialUpdate(facility: PartialUpdateFacility): Observable<EntityResponseType> {
    return this.http.patch<IFacility>(`${this.resourceUrl}/${this.getFacilityIdentifier(facility)}`, facility, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFacility>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFacility[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFacility[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getFacilityIdentifier(facility: Pick<IFacility, 'id'>): number {
    return facility.id;
  }

  compareFacility(o1: Pick<IFacility, 'id'> | null, o2: Pick<IFacility, 'id'> | null): boolean {
    return o1 && o2 ? this.getFacilityIdentifier(o1) === this.getFacilityIdentifier(o2) : o1 === o2;
  }

  addFacilityToCollectionIfMissing<Type extends Pick<IFacility, 'id'>>(
    facilityCollection: Type[],
    ...facilitiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const facilities: Type[] = facilitiesToCheck.filter(isPresent);
    if (facilities.length > 0) {
      const facilityCollectionIdentifiers = facilityCollection.map(facilityItem => this.getFacilityIdentifier(facilityItem)!);
      const facilitiesToAdd = facilities.filter(facilityItem => {
        const facilityIdentifier = this.getFacilityIdentifier(facilityItem);
        if (facilityCollectionIdentifiers.includes(facilityIdentifier)) {
          return false;
        }
        facilityCollectionIdentifiers.push(facilityIdentifier);
        return true;
      });
      return [...facilitiesToAdd, ...facilityCollection];
    }
    return facilityCollection;
  }
}
