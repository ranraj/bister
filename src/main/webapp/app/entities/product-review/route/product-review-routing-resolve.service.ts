import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductReview } from '../product-review.model';
import { ProductReviewService } from '../service/product-review.service';

@Injectable({ providedIn: 'root' })
export class ProductReviewRoutingResolveService implements Resolve<IProductReview | null> {
  constructor(protected service: ProductReviewService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductReview | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productReview: HttpResponse<IProductReview>) => {
          if (productReview.body) {
            return of(productReview.body);
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
