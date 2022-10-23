import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductVariationAttributeTerm } from '../product-variation-attribute-term.model';
import { ProductVariationAttributeTermService } from '../service/product-variation-attribute-term.service';

@Injectable({ providedIn: 'root' })
export class ProductVariationAttributeTermRoutingResolveService implements Resolve<IProductVariationAttributeTerm | null> {
  constructor(protected service: ProductVariationAttributeTermService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductVariationAttributeTerm | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productVariationAttributeTerm: HttpResponse<IProductVariationAttributeTerm>) => {
          if (productVariationAttributeTerm.body) {
            return of(productVariationAttributeTerm.body);
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
