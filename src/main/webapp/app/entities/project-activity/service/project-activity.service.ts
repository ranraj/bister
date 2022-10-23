import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProjectActivity, NewProjectActivity } from '../project-activity.model';

export type PartialUpdateProjectActivity = Partial<IProjectActivity> & Pick<IProjectActivity, 'id'>;

export type EntityResponseType = HttpResponse<IProjectActivity>;
export type EntityArrayResponseType = HttpResponse<IProjectActivity[]>;

@Injectable({ providedIn: 'root' })
export class ProjectActivityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/project-activities');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/project-activities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(projectActivity: NewProjectActivity): Observable<EntityResponseType> {
    return this.http.post<IProjectActivity>(this.resourceUrl, projectActivity, { observe: 'response' });
  }

  update(projectActivity: IProjectActivity): Observable<EntityResponseType> {
    return this.http.put<IProjectActivity>(`${this.resourceUrl}/${this.getProjectActivityIdentifier(projectActivity)}`, projectActivity, {
      observe: 'response',
    });
  }

  partialUpdate(projectActivity: PartialUpdateProjectActivity): Observable<EntityResponseType> {
    return this.http.patch<IProjectActivity>(`${this.resourceUrl}/${this.getProjectActivityIdentifier(projectActivity)}`, projectActivity, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProjectActivity>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectActivity[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectActivity[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProjectActivityIdentifier(projectActivity: Pick<IProjectActivity, 'id'>): number {
    return projectActivity.id;
  }

  compareProjectActivity(o1: Pick<IProjectActivity, 'id'> | null, o2: Pick<IProjectActivity, 'id'> | null): boolean {
    return o1 && o2 ? this.getProjectActivityIdentifier(o1) === this.getProjectActivityIdentifier(o2) : o1 === o2;
  }

  addProjectActivityToCollectionIfMissing<Type extends Pick<IProjectActivity, 'id'>>(
    projectActivityCollection: Type[],
    ...projectActivitiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const projectActivities: Type[] = projectActivitiesToCheck.filter(isPresent);
    if (projectActivities.length > 0) {
      const projectActivityCollectionIdentifiers = projectActivityCollection.map(
        projectActivityItem => this.getProjectActivityIdentifier(projectActivityItem)!
      );
      const projectActivitiesToAdd = projectActivities.filter(projectActivityItem => {
        const projectActivityIdentifier = this.getProjectActivityIdentifier(projectActivityItem);
        if (projectActivityCollectionIdentifiers.includes(projectActivityIdentifier)) {
          return false;
        }
        projectActivityCollectionIdentifiers.push(projectActivityIdentifier);
        return true;
      });
      return [...projectActivitiesToAdd, ...projectActivityCollection];
    }
    return projectActivityCollection;
  }
}
