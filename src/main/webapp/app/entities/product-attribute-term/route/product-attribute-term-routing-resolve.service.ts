import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductAttributeTerm } from '../product-attribute-term.model';
import { ProductAttributeTermService } from '../service/product-attribute-term.service';

@Injectable({ providedIn: 'root' })
export class ProductAttributeTermRoutingResolveService implements Resolve<IProductAttributeTerm | null> {
  constructor(protected service: ProductAttributeTermService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductAttributeTerm | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productAttributeTerm: HttpResponse<IProductAttributeTerm>) => {
          if (productAttributeTerm.body) {
            return of(productAttributeTerm.body);
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
