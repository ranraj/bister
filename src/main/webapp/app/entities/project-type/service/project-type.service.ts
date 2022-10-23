import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProjectType, NewProjectType } from '../project-type.model';

export type PartialUpdateProjectType = Partial<IProjectType> & Pick<IProjectType, 'id'>;

export type EntityResponseType = HttpResponse<IProjectType>;
export type EntityArrayResponseType = HttpResponse<IProjectType[]>;

@Injectable({ providedIn: 'root' })
export class ProjectTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/project-types');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/project-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(projectType: NewProjectType): Observable<EntityResponseType> {
    return this.http.post<IProjectType>(this.resourceUrl, projectType, { observe: 'response' });
  }

  update(projectType: IProjectType): Observable<EntityResponseType> {
    return this.http.put<IProjectType>(`${this.resourceUrl}/${this.getProjectTypeIdentifier(projectType)}`, projectType, {
      observe: 'response',
    });
  }

  partialUpdate(projectType: PartialUpdateProjectType): Observable<EntityResponseType> {
    return this.http.patch<IProjectType>(`${this.resourceUrl}/${this.getProjectTypeIdentifier(projectType)}`, projectType, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProjectType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectType[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProjectTypeIdentifier(projectType: Pick<IProjectType, 'id'>): number {
    return projectType.id;
  }

  compareProjectType(o1: Pick<IProjectType, 'id'> | null, o2: Pick<IProjectType, 'id'> | null): boolean {
    return o1 && o2 ? this.getProjectTypeIdentifier(o1) === this.getProjectTypeIdentifier(o2) : o1 === o2;
  }

  addProjectTypeToCollectionIfMissing<Type extends Pick<IProjectType, 'id'>>(
    projectTypeCollection: Type[],
    ...projectTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const projectTypes: Type[] = projectTypesToCheck.filter(isPresent);
    if (projectTypes.length > 0) {
      const projectTypeCollectionIdentifiers = projectTypeCollection.map(
        projectTypeItem => this.getProjectTypeIdentifier(projectTypeItem)!
      );
      const projectTypesToAdd = projectTypes.filter(projectTypeItem => {
        const projectTypeIdentifier = this.getProjectTypeIdentifier(projectTypeItem);
        if (projectTypeCollectionIdentifiers.includes(projectTypeIdentifier)) {
          return false;
        }
        projectTypeCollectionIdentifiers.push(projectTypeIdentifier);
        return true;
      });
      return [...projectTypesToAdd, ...projectTypeCollection];
    }
    return projectTypeCollection;
  }
}
