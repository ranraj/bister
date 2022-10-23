import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPromotion } from '../promotion.model';
import { PromotionService } from '../service/promotion.service';

@Injectable({ providedIn: 'root' })
export class PromotionRoutingResolveService implements Resolve<IPromotion | null> {
  constructor(protected service: PromotionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPromotion | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((promotion: HttpResponse<IPromotion>) => {
          if (promotion.body) {
            return of(promotion.body);
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
