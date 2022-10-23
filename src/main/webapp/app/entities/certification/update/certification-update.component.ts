import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CertificationFormService, CertificationFormGroup } from './certification-form.service';
import { ICertification } from '../certification.model';
import { CertificationService } from '../service/certification.service';
import { CertificationStatus } from 'app/entities/enumerations/certification-status.model';

@Component({
  selector: 'yali-certification-update',
  templateUrl: './certification-update.component.html',
})
export class CertificationUpdateComponent implements OnInit {
  isSaving = false;
  certification: ICertification | null = null;
  certificationStatusValues = Object.keys(CertificationStatus);

  editForm: CertificationFormGroup = this.certificationFormService.createCertificationFormGroup();

  constructor(
    protected certificationService: CertificationService,
    protected certificationFormService: CertificationFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ certification }) => {
      this.certification = certification;
      if (certification) {
        this.updateForm(certification);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const certification = this.certificationFormService.getCertification(this.editForm);
    if (certification.id !== null) {
      this.subscribeToSaveResponse(this.certificationService.update(certification));
    } else {
      this.subscribeToSaveResponse(this.certificationService.create(certification));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICertification>>): void {
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

  protected updateForm(certification: ICertification): void {
    this.certification = certification;
    this.certificationFormService.resetForm(this.editForm, certification);
  }
}
