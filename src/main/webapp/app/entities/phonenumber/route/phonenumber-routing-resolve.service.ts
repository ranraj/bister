import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPhonenumber } from '../phonenumber.model';
import { PhonenumberService } from '../service/phonenumber.service';

@Injectable({ providedIn: 'root' })
export class PhonenumberRoutingResolveService implements Resolve<IPhonenumber | null> {
  constructor(protected service: PhonenumberService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPhonenumber | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((phonenumber: HttpResponse<IPhonenumber>) => {
          if (phonenumber.body) {
            return of(phonenumber.body);
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
