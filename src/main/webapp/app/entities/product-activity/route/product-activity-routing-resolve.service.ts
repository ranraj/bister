import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductActivity } from '../product-activity.model';
import { ProductActivityService } from '../service/product-activity.service';

@Injectable({ providedIn: 'root' })
export class ProductActivityRoutingResolveService implements Resolve<IProductActivity | null> {
  constructor(protected service: ProductActivityService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductActivity | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productActivity: HttpResponse<IProductActivity>) => {
          if (productActivity.body) {
            return of(productActivity.body);
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
