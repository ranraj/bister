import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { BusinessPartnerFormService, BusinessPartnerFormGroup } from './business-partner-form.service';
import { IBusinessPartner } from '../business-partner.model';
import { BusinessPartnerService } from '../service/business-partner.service';

@Component({
  selector: 'yali-business-partner-update',
  templateUrl: './business-partner-update.component.html',
})
export class BusinessPartnerUpdateComponent implements OnInit {
  isSaving = false;
  businessPartner: IBusinessPartner | null = null;

  editForm: BusinessPartnerFormGroup = this.businessPartnerFormService.createBusinessPartnerFormGroup();

  constructor(
    protected businessPartnerService: BusinessPartnerService,
    protected businessPartnerFormService: BusinessPartnerFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ businessPartner }) => {
      this.businessPartner = businessPartner;
      if (businessPartner) {
        this.updateForm(businessPartner);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const businessPartner = this.businessPartnerFormService.getBusinessPartner(this.editForm);
    if (businessPartner.id !== null) {
      this.subscribeToSaveResponse(this.businessPartnerService.update(businessPartner));
    } else {
      this.subscribeToSaveResponse(this.businessPartnerService.create(businessPartner));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBusinessPartner>>): void {
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

  protected updateForm(businessPartner: IBusinessPartner): void {
    this.businessPartner = businessPartner;
    this.businessPartnerFormService.resetForm(this.editForm, businessPartner);
  }
}
