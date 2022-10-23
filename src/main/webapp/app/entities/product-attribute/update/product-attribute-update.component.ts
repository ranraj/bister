import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ProductAttributeFormService, ProductAttributeFormGroup } from './product-attribute-form.service';
import { IProductAttribute } from '../product-attribute.model';
import { ProductAttributeService } from '../service/product-attribute.service';

@Component({
  selector: 'yali-product-attribute-update',
  templateUrl: './product-attribute-update.component.html',
})
export class ProductAttributeUpdateComponent implements OnInit {
  isSaving = false;
  productAttribute: IProductAttribute | null = null;

  editForm: ProductAttributeFormGroup = this.productAttributeFormService.createProductAttributeFormGroup();

  constructor(
    protected productAttributeService: ProductAttributeService,
    protected productAttributeFormService: ProductAttributeFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productAttribute }) => {
      this.productAttribute = productAttribute;
      if (productAttribute) {
        this.updateForm(productAttribute);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productAttribute = this.productAttributeFormService.getProductAttribute(this.editForm);
    if (productAttribute.id !== null) {
      this.subscribeToSaveResponse(this.productAttributeService.update(productAttribute));
    } else {
      this.subscribeToSaveResponse(this.productAttributeService.create(productAttribute));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductAttribute>>): void {
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

  protected updateForm(productAttribute: IProductAttribute): void {
    this.productAttribute = productAttribute;
    this.productAttributeFormService.resetForm(this.editForm, productAttribute);
  }
}
