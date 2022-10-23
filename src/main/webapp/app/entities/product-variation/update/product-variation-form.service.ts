import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProductVariation, NewProductVariation } from '../product-variation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductVariation for edit and NewProductVariationFormGroupInput for create.
 */
type ProductVariationFormGroupInput = IProductVariation | PartialWithRequiredKeyOf<NewProductVariation>;

type ProductVariationFormDefaults = Pick<NewProductVariation, 'id' | 'isDraft' | 'useParentDetails'>;

type ProductVariationFormGroupContent = {
  id: FormControl<IProductVariation['id'] | NewProductVariation['id']>;
  assetId: FormControl<IProductVariation['assetId']>;
  name: FormControl<IProductVariation['name']>;
  description: FormControl<IProductVariation['description']>;
  regularPrice: FormControl<IProductVariation['regularPrice']>;
  salePrice: FormControl<IProductVariation['salePrice']>;
  dateOnSaleFrom: FormControl<IProductVariation['dateOnSaleFrom']>;
  dateOnSaleTo: FormControl<IProductVariation['dateOnSaleTo']>;
  isDraft: FormControl<IProductVariation['isDraft']>;
  useParentDetails: FormControl<IProductVariation['useParentDetails']>;
  saleStatus: FormControl<IProductVariation['saleStatus']>;
  product: FormControl<IProductVariation['product']>;
};

export type ProductVariationFormGroup = FormGroup<ProductVariationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductVariationFormService {
  createProductVariationFormGroup(productVariation: ProductVariationFormGroupInput = { id: null }): ProductVariationFormGroup {
    const productVariationRawValue = {
      ...this.getFormDefaults(),
      ...productVariation,
    };
    return new FormGroup<ProductVariationFormGroupContent>({
      id: new FormControl(
        { value: productVariationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      assetId: new FormControl(productVariationRawValue.assetId),
      name: new FormControl(productVariationRawValue.name, {
        validators: [Validators.required, Validators.minLength(1), Validators.maxLength(100)],
      }),
      description: new FormControl(productVariationRawValue.description, {
        validators: [Validators.required, Validators.minLength(10), Validators.maxLength(1000)],
      }),
      regularPrice: new FormControl(productVariationRawValue.regularPrice, {
        validators: [Validators.required],
      }),
      salePrice: new FormControl(productVariationRawValue.salePrice, {
        validators: [Validators.required],
      }),
      dateOnSaleFrom: new FormControl(productVariationRawValue.dateOnSaleFrom, {
        validators: [Validators.required],
      }),
      dateOnSaleTo: new FormControl(productVariationRawValue.dateOnSaleTo, {
        validators: [Validators.required],
      }),
      isDraft: new FormControl(productVariationRawValue.isDraft, {
        validators: [Validators.required],
      }),
      useParentDetails: new FormControl(productVariationRawValue.useParentDetails, {
        validators: [Validators.required],
      }),
      saleStatus: new FormControl(productVariationRawValue.saleStatus, {
        validators: [Validators.required],
      }),
      product: new FormControl(productVariationRawValue.product, {
        validators: [Validators.required],
      }),
    });
  }

  getProductVariation(form: ProductVariationFormGroup): IProductVariation | NewProductVariation {
    return form.getRawValue() as IProductVariation | NewProductVariation;
  }

  resetForm(form: ProductVariationFormGroup, productVariation: ProductVariationFormGroupInput): void {
    const productVariationRawValue = { ...this.getFormDefaults(), ...productVariation };
    form.reset(
      {
        ...productVariationRawValue,
        id: { value: productVariationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductVariationFormDefaults {
    return {
      id: null,
      isDraft: false,
      useParentDetails: false,
    };
  }
}
