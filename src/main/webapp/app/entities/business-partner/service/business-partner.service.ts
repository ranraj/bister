import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IBusinessPartner, NewBusinessPartner } from '../business-partner.model';

export type PartialUpdateBusinessPartner = Partial<IBusinessPartner> & Pick<IBusinessPartner, 'id'>;

export type EntityResponseType = HttpResponse<IBusinessPartner>;
export type EntityArrayResponseType = HttpResponse<IBusinessPartner[]>;

@Injectable({ providedIn: 'root' })
export class BusinessPartnerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/business-partners');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/business-partners');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(businessPartner: NewBusinessPartner): Observable<EntityResponseType> {
    return this.http.post<IBusinessPartner>(this.resourceUrl, businessPartner, { observe: 'response' });
  }

  update(businessPartner: IBusinessPartner): Observable<EntityResponseType> {
    return this.http.put<IBusinessPartner>(`${this.resourceUrl}/${this.getBusinessPartnerIdentifier(businessPartner)}`, businessPartner, {
      observe: 'response',
    });
  }

  partialUpdate(businessPartner: PartialUpdateBusinessPartner): Observable<EntityResponseType> {
    return this.http.patch<IBusinessPartner>(`${this.resourceUrl}/${this.getBusinessPartnerIdentifier(businessPartner)}`, businessPartner, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBusinessPartner>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBusinessPartner[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBusinessPartner[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getBusinessPartnerIdentifier(businessPartner: Pick<IBusinessPartner, 'id'>): number {
    return businessPartner.id;
  }

  compareBusinessPartner(o1: Pick<IBusinessPartner, 'id'> | null, o2: Pick<IBusinessPartner, 'id'> | null): boolean {
    return o1 && o2 ? this.getBusinessPartnerIdentifier(o1) === this.getBusinessPartnerIdentifier(o2) : o1 === o2;
  }

  addBusinessPartnerToCollectionIfMissing<Type extends Pick<IBusinessPartner, 'id'>>(
    businessPartnerCollection: Type[],
    ...businessPartnersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const businessPartners: Type[] = businessPartnersToCheck.filter(isPresent);
    if (businessPartners.length > 0) {
      const businessPartnerCollectionIdentifiers = businessPartnerCollection.map(
        businessPartnerItem => this.getBusinessPartnerIdentifier(businessPartnerItem)!
      );
      const businessPartnersToAdd = businessPartners.filter(businessPartnerItem => {
        const businessPartnerIdentifier = this.getBusinessPartnerIdentifier(businessPartnerItem);
        if (businessPartnerCollectionIdentifiers.includes(businessPartnerIdentifier)) {
          return false;
        }
        businessPartnerCollectionIdentifiers.push(businessPartnerIdentifier);
        return true;
      });
      return [...businessPartnersToAdd, ...businessPartnerCollection];
    }
    return businessPartnerCollection;
  }
}
