import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductVariation } from '../product-variation.model';
import { ProductVariationService } from '../service/product-variation.service';

@Injectable({ providedIn: 'root' })
export class ProductVariationRoutingResolveService implements Resolve<IProductVariation | null> {
  constructor(protected service: ProductVariationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductVariation | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productVariation: HttpResponse<IProductVariation>) => {
          if (productVariation.body) {
            return of(productVariation.body);
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
