import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductSpecificationGroup } from '../product-specification-group.model';
import { ProductSpecificationGroupService } from '../service/product-specification-group.service';

@Injectable({ providedIn: 'root' })
export class ProductSpecificationGroupRoutingResolveService implements Resolve<IProductSpecificationGroup | null> {
  constructor(protected service: ProductSpecificationGroupService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductSpecificationGroup | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productSpecificationGroup: HttpResponse<IProductSpecificationGroup>) => {
          if (productSpecificationGroup.body) {
            return of(productSpecificationGroup.body);
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
