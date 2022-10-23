import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEnquiryResponse } from '../enquiry-response.model';
import { EnquiryResponseService } from '../service/enquiry-response.service';

@Injectable({ providedIn: 'root' })
export class EnquiryResponseRoutingResolveService implements Resolve<IEnquiryResponse | null> {
  constructor(protected service: EnquiryResponseService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEnquiryResponse | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((enquiryResponse: HttpResponse<IEnquiryResponse>) => {
          if (enquiryResponse.body) {
            return of(enquiryResponse.body);
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
