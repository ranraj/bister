import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITaxClass } from '../tax-class.model';
import { TaxClassService } from '../service/tax-class.service';

@Injectable({ providedIn: 'root' })
export class TaxClassRoutingResolveService implements Resolve<ITaxClass | null> {
  constructor(protected service: TaxClassService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITaxClass | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((taxClass: HttpResponse<ITaxClass>) => {
          if (taxClass.body) {
            return of(taxClass.body);
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
