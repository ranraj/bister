import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProjectReview } from '../project-review.model';
import { ProjectReviewService } from '../service/project-review.service';

@Injectable({ providedIn: 'root' })
export class ProjectReviewRoutingResolveService implements Resolve<IProjectReview | null> {
  constructor(protected service: ProjectReviewService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProjectReview | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((projectReview: HttpResponse<IProjectReview>) => {
          if (projectReview.body) {
            return of(projectReview.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
