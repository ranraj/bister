import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBookingOrder } from '../booking-order.model';
import { BookingOrderService } from '../service/booking-order.service';

@Injectable({ providedIn: 'root' })
export class BookingOrderRoutingResolveService implements Resolve<IBookingOrder | null> {
  constructor(protected service: BookingOrderService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBookingOrder | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((bookingOrder: HttpResponse<IBookingOrder>) => {
          if (bookingOrder.body) {
            return of(bookingOrder.body);
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
