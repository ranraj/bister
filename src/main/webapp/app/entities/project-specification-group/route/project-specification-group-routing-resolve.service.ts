import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProjectSpecificationGroup } from '../project-specification-group.model';
import { ProjectSpecificationGroupService } from '../service/project-specification-group.service';

@Injectable({ providedIn: 'root' })
export class ProjectSpecificationGroupRoutingResolveService implements Resolve<IProjectSpecificationGroup | null> {
  constructor(protected service: ProjectSpecificationGroupService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProjectSpecificationGroup | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((projectSpecificationGroup: HttpResponse<IProjectSpecificationGroup>) => {
          if (projectSpecificationGroup.body) {
            return of(projectSpecificationGroup.body);
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
