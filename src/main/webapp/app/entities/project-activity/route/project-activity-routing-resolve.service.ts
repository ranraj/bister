import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProjectActivity } from '../project-activity.model';
import { ProjectActivityService } from '../service/project-activity.service';

@Injectable({ providedIn: 'root' })
export class ProjectActivityRoutingResolveService implements Resolve<IProjectActivity | null> {
  constructor(protected service: ProjectActivityService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProjectActivity | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((projectActivity: HttpResponse<IProjectActivity>) => {
          if (projectActivity.body) {
            return of(projectActivity.body);
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
