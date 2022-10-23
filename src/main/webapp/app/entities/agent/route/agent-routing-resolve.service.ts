import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAgent } from '../agent.model';
import { AgentService } from '../service/agent.service';

@Injectable({ providedIn: 'root' })
export class AgentRoutingResolveService implements Resolve<IAgent | null> {
  constructor(protected service: AgentService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAgent | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((agent: HttpResponse<IAgent>) => {
          if (agent.body) {
            return of(agent.body);
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
