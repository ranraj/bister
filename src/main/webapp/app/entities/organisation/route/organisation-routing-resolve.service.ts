import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrganisation } from '../organisation.model';
import { OrganisationService } from '../service/organisation.service';

@Injectable({ providedIn: 'root' })
export class OrganisationRoutingResolveService implements Resolve<IOrganisation | null> {
  constructor(protected service: OrganisationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrganisation | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((organisation: HttpResponse<IOrganisation>) => {
          if (organisation.body) {
            return of(organisation.body);
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
