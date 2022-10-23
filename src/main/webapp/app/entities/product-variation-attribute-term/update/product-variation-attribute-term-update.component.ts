import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import {
  ProductVariationAttributeTermFormService,
  ProductVariationAttributeTermFormGroup,
} from './product-variation-attribute-term-form.service';
import { IProductVariationAttributeTerm } from '../product-variation-attribute-term.model';
import { ProductVariationAttributeTermService } from '../service/product-variation-attribute-term.service';
import { IProductVariation } from 'app/entities/product-variation/product-variation.model';
import { ProductVariationService } from 'app/entities/product-variation/service/product-variation.service';

@Component({
  selector: 'yali-product-variation-attribute-term-update',
  templateUrl: './product-variation-attribute-term-update.component.html',
})
export class ProductVariationAttributeTermUpdateComponent implements OnInit {
  isSaving = false;
  productVariationAttributeTerm: IProductVariationAttributeTerm | null = null;

  productVariationsSharedCollection: IProductVariation[] = [];

  editForm: ProductVariationAttributeTermFormGroup =
    this.productVariationAttributeTermFormService.createProductVariationAttributeTermFormGroup();

  constructor(
    protected productVariationAttributeTermService: ProductVariationAttributeTermService,
    protected productVariationAttributeTermFormService: ProductVariationAttributeTermFormService,
    protected productVariationService: ProductVariationService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProductVariation = (o1: IProductVariation | null, o2: IProductVariation | null): boolean =>
    this.productVariationService.compareProductVariation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productVariationAttributeTerm }) => {
      this.productVariationAttributeTerm = productVariationAttributeTerm;
      if (productVariationAttributeTerm) {
        this.updateForm(productVariationAttributeTerm);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productVariationAttributeTerm = this.productVariationAttributeTermFormService.getProductVariationAttributeTerm(this.editForm);
    if (productVariationAttributeTerm.id !== null) {
      this.subscribeToSaveResponse(this.productVariationAttributeTermService.update(productVariationAttributeTerm));
    } else {
      this.subscribeToSaveResponse(this.productVariationAttributeTermService.create(productVariationAttributeTerm));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductVariationAttributeTerm>>): void {
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

  protected updateForm(productVariationAttributeTerm: IProductVariationAttributeTerm): void {
    this.productVariationAttributeTerm = productVariationAttributeTerm;
    this.productVariationAttributeTermFormService.resetForm(this.editForm, productVariationAttributeTerm);

    this.productVariationsSharedCollection = this.productVariationService.addProductVariationToCollectionIfMissing<IProductVariation>(
      this.productVariationsSharedCollection,
      productVariationAttributeTerm.productVariation
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productVariationService
      .query()
      .pipe(map((res: HttpResponse<IProductVariation[]>) => res.body ?? []))
      .pipe(
        map((productVariations: IProductVariation[]) =>
          this.productVariationService.addProductVariationToCollectionIfMissing<IProductVariation>(
            productVariations,
            this.productVariationAttributeTerm?.productVariation
          )
        )
      )
      .subscribe((productVariations: IProductVariation[]) => (this.productVariationsSharedCollection = productVariations));
  }
}
