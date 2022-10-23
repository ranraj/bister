import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProjectReview, NewProjectReview } from '../project-review.model';

export type PartialUpdateProjectReview = Partial<IProjectReview> & Pick<IProjectReview, 'id'>;

export type EntityResponseType = HttpResponse<IProjectReview>;
export type EntityArrayResponseType = HttpResponse<IProjectReview[]>;

@Injectable({ providedIn: 'root' })
export class ProjectReviewService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/project-reviews');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/project-reviews');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(projectReview: NewProjectReview): Observable<EntityResponseType> {
    return this.http.post<IProjectReview>(this.resourceUrl, projectReview, { observe: 'response' });
  }

  update(projectReview: IProjectReview): Observable<EntityResponseType> {
    return this.http.put<IProjectReview>(`${this.resourceUrl}/${this.getProjectReviewIdentifier(projectReview)}`, projectReview, {
      observe: 'response',
    });
  }

  partialUpdate(projectReview: PartialUpdateProjectReview): Observable<EntityResponseType> {
    return this.http.patch<IProjectReview>(`${this.resourceUrl}/${this.getProjectReviewIdentifier(projectReview)}`, projectReview, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProjectReview>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectReview[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjectReview[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProjectReviewIdentifier(projectReview: Pick<IProjectReview, 'id'>): number {
    return projectReview.id;
  }

  compareProjectReview(o1: Pick<IProjectReview, 'id'> | null, o2: Pick<IProjectReview, 'id'> | null): boolean {
    return o1 && o2 ? this.getProjectReviewIdentifier(o1) === this.getProjectReviewIdentifier(o2) : o1 === o2;
  }

  addProjectReviewToCollectionIfMissing<Type extends Pick<IProjectReview, 'id'>>(
    projectReviewCollection: Type[],
    ...projectReviewsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const projectReviews: Type[] = projectReviewsToCheck.filter(isPresent);
    if (projectReviews.length > 0) {
      const projectReviewCollectionIdentifiers = projectReviewCollection.map(
        projectReviewItem => this.getProjectReviewIdentifier(projectReviewItem)!
      );
      const projectReviewsToAdd = projectReviews.filter(projectReviewItem => {
        const projectReviewIdentifier = this.getProjectReviewIdentifier(projectReviewItem);
        if (projectReviewCollectionIdentifiers.includes(projectReviewIdentifier)) {
          return false;
        }
        projectReviewCollectionIdentifiers.push(projectReviewIdentifier);
        return true;
      });
      return [...projectReviewsToAdd, ...projectReviewCollection];
    }
    return projectReviewCollection;
  }
}
