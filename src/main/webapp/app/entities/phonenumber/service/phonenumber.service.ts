import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPhonenumber, NewPhonenumber } from '../phonenumber.model';

export type PartialUpdatePhonenumber = Partial<IPhonenumber> & Pick<IPhonenumber, 'id'>;

export type EntityResponseType = HttpResponse<IPhonenumber>;
export type EntityArrayResponseType = HttpResponse<IPhonenumber[]>;

@Injectable({ providedIn: 'root' })
export class PhonenumberService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/phonenumbers');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/phonenumbers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(phonenumber: NewPhonenumber): Observable<EntityResponseType> {
    return this.http.post<IPhonenumber>(this.resourceUrl, phonenumber, { observe: 'response' });
  }

  update(phonenumber: IPhonenumber): Observable<EntityResponseType> {
    return this.http.put<IPhonenumber>(`${this.resourceUrl}/${this.getPhonenumberIdentifier(phonenumber)}`, phonenumber, {
      observe: 'response',
    });
  }

  partialUpdate(phonenumber: PartialUpdatePhonenumber): Observable<EntityResponseType> {
    return this.http.patch<IPhonenumber>(`${this.resourceUrl}/${this.getPhonenumberIdentifier(phonenumber)}`, phonenumber, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPhonenumber>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPhonenumber[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPhonenumber[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getPhonenumberIdentifier(phonenumber: Pick<IPhonenumber, 'id'>): number {
    return phonenumber.id;
  }

  comparePhonenumber(o1: Pick<IPhonenumber, 'id'> | null, o2: Pick<IPhonenumber, 'id'> | null): boolean {
    return o1 && o2 ? this.getPhonenumberIdentifier(o1) === this.getPhonenumberIdentifier(o2) : o1 === o2;
  }

  addPhonenumberToCollectionIfMissing<Type extends Pick<IPhonenumber, 'id'>>(
    phonenumberCollection: Type[],
    ...phonenumbersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const phonenumbers: Type[] = phonenumbersToCheck.filter(isPresent);
    if (phonenumbers.length > 0) {
      const phonenumberCollectionIdentifiers = phonenumberCollection.map(
        phonenumberItem => this.getPhonenumberIdentifier(phonenumberItem)!
      );
      const phonenumbersToAdd = phonenumbers.filter(phonenumberItem => {
        const phonenumberIdentifier = this.getPhonenumberIdentifier(phonenumberItem);
        if (phonenumberCollectionIdentifiers.includes(phonenumberIdentifier)) {
          return false;
        }
        phonenumberCollectionIdentifiers.push(phonenumberIdentifier);
        return true;
      });
      return [...phonenumbersToAdd, ...phonenumberCollection];
    }
    return phonenumberCollection;
  }
}
