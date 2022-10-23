import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EnquiryResponseFormService, EnquiryResponseFormGroup } from './enquiry-response-form.service';
import { IEnquiryResponse } from '../enquiry-response.model';
import { EnquiryResponseService } from '../service/enquiry-response.service';
import { IAgent } from 'app/entities/agent/agent.model';
import { AgentService } from 'app/entities/agent/service/agent.service';
import { IEnquiry } from 'app/entities/enquiry/enquiry.model';
import { EnquiryService } from 'app/entities/enquiry/service/enquiry.service';
import { EnquiryResponseType } from 'app/entities/enumerations/enquiry-response-type.model';

@Component({
  selector: 'yali-enquiry-response-update',
  templateUrl: './enquiry-response-update.component.html',
})
export class EnquiryResponseUpdateComponent implements OnInit {
  isSaving = false;
  enquiryResponse: IEnquiryResponse | null = null;
  enquiryResponseTypeValues = Object.keys(EnquiryResponseType);

  agentsSharedCollection: IAgent[] = [];
  enquiriesSharedCollection: IEnquiry[] = [];

  editForm: EnquiryResponseFormGroup = this.enquiryResponseFormService.createEnquiryResponseFormGroup();

  constructor(
    protected enquiryResponseService: EnquiryResponseService,
    protected enquiryResponseFormService: EnquiryResponseFormService,
    protected agentService: AgentService,
    protected enquiryService: EnquiryService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareAgent = (o1: IAgent | null, o2: IAgent | null): boolean => this.agentService.compareAgent(o1, o2);

  compareEnquiry = (o1: IEnquiry | null, o2: IEnquiry | null): boolean => this.enquiryService.compareEnquiry(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ enquiryResponse }) => {
      this.enquiryResponse = enquiryResponse;
      if (enquiryResponse) {
        this.updateForm(enquiryResponse);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const enquiryResponse = this.enquiryResponseFormService.getEnquiryResponse(this.editForm);
    if (enquiryResponse.id !== null) {
      this.subscribeToSaveResponse(this.enquiryResponseService.update(enquiryResponse));
    } else {
      this.subscribeToSaveResponse(this.enquiryResponseService.create(enquiryResponse));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEnquiryResponse>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(enquiryResponse: IEnquiryResponse): void {
    this.enquiryResponse = enquiryResponse;
    this.enquiryResponseFormService.resetForm(this.editForm, enquiryResponse);

    this.agentsSharedCollection = this.agentService.addAgentToCollectionIfMissing<IAgent>(
      this.agentsSharedCollection,
      enquiryResponse.agent
    );
    this.enquiriesSharedCollection = this.enquiryService.addEnquiryToCollectionIfMissing<IEnquiry>(
      this.enquiriesSharedCollection,
      enquiryResponse.enquiry
    );
  }

  protected loadRelationshipsOptions(): void {
    this.agentService
      .query()
      .pipe(map((res: HttpResponse<IAgent[]>) => res.body ?? []))
      .pipe(map((agents: IAgent[]) => this.agentService.addAgentToCollectionIfMissing<IAgent>(agents, this.enquiryResponse?.agent)))
      .subscribe((agents: IAgent[]) => (this.agentsSharedCollection = agents));

    this.enquiryService
      .query()
      .pipe(map((res: HttpResponse<IEnquiry[]>) => res.body ?? []))
      .pipe(
        map((enquiries: IEnquiry[]) =>
          this.enquiryService.addEnquiryToCollectionIfMissing<IEnquiry>(enquiries, this.enquiryResponse?.enquiry)
        )
      )
      .subscribe((enquiries: IEnquiry[]) => (this.enquiriesSharedCollection = enquiries));
  }
}
