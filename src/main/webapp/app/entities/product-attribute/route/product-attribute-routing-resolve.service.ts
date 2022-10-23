import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductAttribute } from '../product-attribute.model';
import { ProductAttributeService } from '../service/product-attribute.service';

@Injectable({ providedIn: 'root' })
export class ProductAttributeRoutingResolveService implements Resolve<IProductAttribute | null> {
  constructor(protected service: ProductAttributeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductAttribute | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productAttribute: HttpResponse<IProductAttribute>) => {
          if (productAttribute.body) {
            return of(productAttribute.body);
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
