import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProjectSpecification, NewProjectSpecification } from '../project-specification.model';

export type PartialUpdateProjectSpecification = Partial<IProjectSpecification> & Pick<IProjectSpecification, 'id'>;

export type EntityResponseType = HttpResponse<IProjectSpecification>;
export type EntityArrayResponseType = HttpResponse<IProjectSpecification[]>;

@Injectable({ providedIn: 'root' })
export class ProjectSpecificationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/project-specifications');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/project-specifications');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(projectSpecification: NewProjectSpecification): Observable<EntityResponseType> {
    return this.http.post<IProjectSpecification>(this.resourceUrl, projectSpecification, { observe: 'response' });
  }

  update(projectSpecification: IProjectSpecification): Observable<EntityResponseType> {
    return this.http.put<IProjectSpecification>(
      `${this.resourceUrl}/${this.getProjectSpecificationIdentifier(projectSpecification)}`,
      projectSpecification,
      { observe: 'response' }
    );
  }

  partialUpdate(projectSpecification: PartialUpdateProjectSpecification): Observable<EntityResponseType> {
    return this.http.patch<IProjectSpecification>(
      `${this.resourceUrl}/${this.getProjectSpecificationIdentifier(projectSpecification)}`,
      projectSpecification,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProjectSpecification>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectSpecification[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectSpecification[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProjectSpecificationIdentifier(projectSpecification: Pick<IProjectSpecification, 'id'>): number {
    return projectSpecification.id;
  }

  compareProjectSpecification(o1: Pick<IProjectSpecification, 'id'> | null, o2: Pick<IProjectSpecification, 'id'> | null): boolean {
    return o1 && o2 ? this.getProjectSpecificationIdentifier(o1) === this.getProjectSpecificationIdentifier(o2) : o1 === o2;
  }

  addProjectSpecificationToCollectionIfMissing<Type extends Pick<IProjectSpecification, 'id'>>(
    projectSpecificationCollection: Type[],
    ...projectSpecificationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const projectSpecifications: Type[] = projectSpecificationsToCheck.filter(isPresent);
    if (projectSpecifications.length > 0) {
      const projectSpecificationCollectionIdentifiers = projectSpecificationCollection.map(
        projectSpecificationItem => this.getProjectSpecificationIdentifier(projectSpecificationItem)!
      );
      const projectSpecificationsToAdd = projectSpecifications.filter(projectSpecificationItem => {
        const projectSpecificationIdentifier = this.getProjectSpecificationIdentifier(projectSpecificationItem);
        if (projectSpecificationCollectionIdentifiers.includes(projectSpecificationIdentifier)) {
          return false;
        }
        projectSpecificationCollectionIdentifiers.push(projectSpecificationIdentifier);
        return true;
      });
      return [...projectSpecificationsToAdd, ...projectSpecificationCollection];
    }
    return projectSpecificationCollection;
  }
}
