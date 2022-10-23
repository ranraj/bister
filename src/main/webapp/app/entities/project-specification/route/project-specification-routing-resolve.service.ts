import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProjectSpecification } from '../project-specification.model';
import { ProjectSpecificationService } from '../service/project-specification.service';

@Injectable({ providedIn: 'root' })
export class ProjectSpecificationRoutingResolveService implements Resolve<IProjectSpecification | null> {
  constructor(protected service: ProjectSpecificationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProjectSpecification | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((projectSpecification: HttpResponse<IProjectSpecification>) => {
          if (projectSpecification.body) {
            return of(projectSpecification.body);
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
