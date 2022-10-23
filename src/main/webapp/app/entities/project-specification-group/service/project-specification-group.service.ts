import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProjectSpecificationGroup, NewProjectSpecificationGroup } from '../project-specification-group.model';

export type PartialUpdateProjectSpecificationGroup = Partial<IProjectSpecificationGroup> & Pick<IProjectSpecificationGroup, 'id'>;

export type EntityResponseType = HttpResponse<IProjectSpecificationGroup>;
export type EntityArrayResponseType = HttpResponse<IProjectSpecificationGroup[]>;

@Injectable({ providedIn: 'root' })
export class ProjectSpecificationGroupService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/project-specification-groups');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/project-specification-groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(projectSpecificationGroup: NewProjectSpecificationGroup): Observable<EntityResponseType> {
    return this.http.post<IProjectSpecificationGroup>(this.resourceUrl, projectSpecificationGroup, { observe: 'response' });
  }

  update(projectSpecificationGroup: IProjectSpecificationGroup): Observable<EntityResponseType> {
    return this.http.put<IProjectSpecificationGroup>(
      `${this.resourceUrl}/${this.getProjectSpecificationGroupIdentifier(projectSpecificationGroup)}`,
      projectSpecificationGroup,
      { observe: 'response' }
    );
  }

  partialUpdate(projectSpecificationGroup: PartialUpdateProjectSpecificationGroup): Observable<EntityResponseType> {
    return this.http.patch<IProjectSpecificationGroup>(
      `${this.resourceUrl}/${this.getProjectSpecificationGroupIdentifier(projectSpecificationGroup)}`,
      projectSpecificationGroup,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProjectSpecificationGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectSpecificationGroup[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectSpecificationGroup[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProjectSpecificationGroupIdentifier(projectSpecificationGroup: Pick<IProjectSpecificationGroup, 'id'>): number {
    return projectSpecificationGroup.id;
  }

  compareProjectSpecificationGroup(
    o1: Pick<IProjectSpecificationGroup, 'id'> | null,
    o2: Pick<IProjectSpecificationGroup, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getProjectSpecificationGroupIdentifier(o1) === this.getProjectSpecificationGroupIdentifier(o2) : o1 === o2;
  }

  addProjectSpecificationGroupToCollectionIfMissing<Type extends Pick<IProjectSpecificationGroup, 'id'>>(
    projectSpecificationGroupCollection: Type[],
    ...projectSpecificationGroupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const projectSpecificationGroups: Type[] = projectSpecificationGroupsToCheck.filter(isPresent);
    if (projectSpecificationGroups.length > 0) {
      const projectSpecificationGroupCollectionIdentifiers = projectSpecificationGroupCollection.map(
        projectSpecificationGroupItem => this.getProjectSpecificationGroupIdentifier(projectSpecificationGroupItem)!
      );
      const projectSpecificationGroupsToAdd = projectSpecificationGroups.filter(projectSpecificationGroupItem => {
        const projectSpecificationGroupIdentifier = this.getProjectSpecificationGroupIdentifier(projectSpecificationGroupItem);
        if (projectSpecificationGroupCollectionIdentifiers.includes(projectSpecificationGroupIdentifier)) {
          return false;
        }
        projectSpecificationGroupCollectionIdentifiers.push(projectSpecificationGroupIdentifier);
        return true;
      });
      return [...projectSpecificationGroupsToAdd, ...projectSpecificationGroupCollection];
    }
    return projectSpecificationGroupCollection;
  }
}
