import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICertification } from '../certification.model';
import { CertificationService } from '../service/certification.service';

@Injectable({ providedIn: 'root' })
export class CertificationRoutingResolveService implements Resolve<ICertification | null> {
  constructor(protected service: CertificationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICertification | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((certification: HttpResponse<ICertification>) => {
          if (certification.body) {
            return of(certification.body);
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
