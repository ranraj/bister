import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProjectType } from '../project-type.model';
import { ProjectTypeService } from '../service/project-type.service';

@Injectable({ providedIn: 'root' })
export class ProjectTypeRoutingResolveService implements Resolve<IProjectType | null> {
  constructor(protected service: ProjectTypeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProjectType | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((projectType: HttpResponse<IProjectType>) => {
          if (projectType.body) {
            return of(projectType.body);
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
