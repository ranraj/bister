import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBusinessPartner } from '../business-partner.model';
import { BusinessPartnerService } from '../service/business-partner.service';

@Injectable({ providedIn: 'root' })
export class BusinessPartnerRoutingResolveService implements Resolve<IBusinessPartner | null> {
  constructor(protected service: BusinessPartnerService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBusinessPartner | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((businessPartner: HttpResponse<IBusinessPartner>) => {
          if (businessPartner.body) {
            return of(businessPartner.body);
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
