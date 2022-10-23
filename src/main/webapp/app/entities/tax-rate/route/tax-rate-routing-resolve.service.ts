import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITaxRate } from '../tax-rate.model';
import { TaxRateService } from '../service/tax-rate.service';

@Injectable({ providedIn: 'root' })
export class TaxRateRoutingResolveService implements Resolve<ITaxRate | null> {
  constructor(protected service: TaxRateService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITaxRate | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((taxRate: HttpResponse<ITaxRate>) => {
          if (taxRate.body) {
            return of(taxRate.body);
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
