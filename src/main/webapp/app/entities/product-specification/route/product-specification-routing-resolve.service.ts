import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductSpecification } from '../product-specification.model';
import { ProductSpecificationService } from '../service/product-specification.service';

@Injectable({ providedIn: 'root' })
export class ProductSpecificationRoutingResolveService implements Resolve<IProductSpecification | null> {
  constructor(protected service: ProductSpecificationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductSpecification | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productSpecification: HttpResponse<IProductSpecification>) => {
          if (productSpecification.body) {
            return of(productSpecification.body);
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
