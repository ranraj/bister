import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPaymentSchedule } from '../payment-schedule.model';
import { PaymentScheduleService } from '../service/payment-schedule.service';

@Injectable({ providedIn: 'root' })
export class PaymentScheduleRoutingResolveService implements Resolve<IPaymentSchedule | null> {
  constructor(protected service: PaymentScheduleService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPaymentSchedule | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((paymentSchedule: HttpResponse<IPaymentSchedule>) => {
          if (paymentSchedule.body) {
            return of(paymentSchedule.body);
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
