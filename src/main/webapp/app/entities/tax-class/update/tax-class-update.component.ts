import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { TaxClassFormService, TaxClassFormGroup } from './tax-class-form.service';
import { ITaxClass } from '../tax-class.model';
import { TaxClassService } from '../service/tax-class.service';

@Component({
  selector: 'yali-tax-class-update',
  templateUrl: './tax-class-update.component.html',
})
export class TaxClassUpdateComponent implements OnInit {
  isSaving = false;
  taxClass: ITaxClass | null = null;

  editForm: TaxClassFormGroup = this.taxClassFormService.createTaxClassFormGroup();

  constructor(
    protected taxClassService: TaxClassService,
    protected taxClassFormService: TaxClassFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taxClass }) => {
      this.taxClass = taxClass;
      if (taxClass) {
        this.updateForm(taxClass);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const taxClass = this.taxClassFormService.getTaxClass(this.editForm);
    if (taxClass.id !== null) {
      this.subscribeToSaveResponse(this.taxClassService.update(taxClass));
    } else {
      this.subscribeToSaveResponse(this.taxClassService.create(taxClass));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITaxClass>>): void {
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

  protected updateForm(taxClass: ITaxClass): void {
    this.taxClass = taxClass;
    this.taxClassFormService.resetForm(this.editForm, taxClass);
  }
}
