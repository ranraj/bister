import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TaxRateFormService, TaxRateFormGroup } from './tax-rate-form.service';
import { ITaxRate } from '../tax-rate.model';
import { TaxRateService } from '../service/tax-rate.service';
import { ITaxClass } from 'app/entities/tax-class/tax-class.model';
import { TaxClassService } from 'app/entities/tax-class/service/tax-class.service';

@Component({
  selector: 'yali-tax-rate-update',
  templateUrl: './tax-rate-update.component.html',
})
export class TaxRateUpdateComponent implements OnInit {
  isSaving = false;
  taxRate: ITaxRate | null = null;

  taxClassesSharedCollection: ITaxClass[] = [];

  editForm: TaxRateFormGroup = this.taxRateFormService.createTaxRateFormGroup();

  constructor(
    protected taxRateService: TaxRateService,
    protected taxRateFormService: TaxRateFormService,
    protected taxClassService: TaxClassService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTaxClass = (o1: ITaxClass | null, o2: ITaxClass | null): boolean => this.taxClassService.compareTaxClass(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taxRate }) => {
      this.taxRate = taxRate;
      if (taxRate) {
        this.updateForm(taxRate);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const taxRate = this.taxRateFormService.getTaxRate(this.editForm);
    if (taxRate.id !== null) {
      this.subscribeToSaveResponse(this.taxRateService.update(taxRate));
    } else {
      this.subscribeToSaveResponse(this.taxRateService.create(taxRate));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITaxRate>>): void {
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

  protected updateForm(taxRate: ITaxRate): void {
    this.taxRate = taxRate;
    this.taxRateFormService.resetForm(this.editForm, taxRate);

    this.taxClassesSharedCollection = this.taxClassService.addTaxClassToCollectionIfMissing<ITaxClass>(
      this.taxClassesSharedCollection,
      taxRate.taxClass
    );
  }

  protected loadRelationshipsOptions(): void {
    this.taxClassService
      .query()
      .pipe(map((res: HttpResponse<ITaxClass[]>) => res.body ?? []))
      .pipe(
        map((taxClasses: ITaxClass[]) =>
          this.taxClassService.addTaxClassToCollectionIfMissing<ITaxClass>(taxClasses, this.taxRate?.taxClass)
        )
      )
      .subscribe((taxClasses: ITaxClass[]) => (this.taxClassesSharedCollection = taxClasses));
  }
}
