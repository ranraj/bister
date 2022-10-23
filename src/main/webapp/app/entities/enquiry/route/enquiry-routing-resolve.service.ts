import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEnquiry } from '../enquiry.model';
import { EnquiryService } from '../service/enquiry.service';

@Injectable({ providedIn: 'root' })
export class EnquiryRoutingResolveService implements Resolve<IEnquiry | null> {
  constructor(protected service: EnquiryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEnquiry | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((enquiry: HttpResponse<IEnquiry>) => {
          if (enquiry.body) {
            return of(enquiry.body);
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
