import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProductVariationAttributeTerm, NewProductVariationAttributeTerm } from '../product-variation-attribute-term.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductVariationAttributeTerm for edit and NewProductVariationAttributeTermFormGroupInput for create.
 */
type ProductVariationAttributeTermFormGroupInput =
  | IProductVariationAttributeTerm
  | PartialWithRequiredKeyOf<NewProductVariationAttributeTerm>;

type ProductVariationAttributeTermFormDefaults = Pick<NewProductVariationAttributeTerm, 'id' | 'overRideProductAttribute'>;

type ProductVariationAttributeTermFormGroupContent = {
  id: FormControl<IProductVariationAttributeTerm['id'] | NewProductVariationAttributeTerm['id']>;
  name: FormControl<IProductVariationAttributeTerm['name']>;
  slug: FormControl<IProductVariationAttributeTerm['slug']>;
  description: FormControl<IProductVariationAttributeTerm['description']>;
  menuOrder: FormControl<IProductVariationAttributeTerm['menuOrder']>;
  overRideProductAttribute: FormControl<IProductVariationAttributeTerm['overRideProductAttribute']>;
  productVariation: FormControl<IProductVariationAttributeTerm['productVariation']>;
};

export type ProductVariationAttributeTermFormGroup = FormGroup<ProductVariationAttributeTermFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductVariationAttributeTermFormService {
  createProductVariationAttributeTermFormGroup(
    productVariationAttributeTerm: ProductVariationAttributeTermFormGroupInput = { id: null }
  ): ProductVariationAttributeTermFormGroup {
    const productVariationAttributeTermRawValue = {
      ...this.getFormDefaults(),
      ...productVariationAttributeTerm,
    };
    return new FormGroup<ProductVariationAttributeTermFormGroupContent>({
      id: new FormControl(
        { value: productVariationAttributeTermRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(productVariationAttributeTermRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      slug: new FormControl(productVariationAttributeTermRawValue.slug, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(productVariationAttributeTermRawValue.description, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(1000)],
      }),
      menuOrder: new FormControl(productVariationAttributeTermRawValue.menuOrder, {
        validators: [Validators.required],
      }),
      overRideProductAttribute: new FormControl(productVariationAttributeTermRawValue.overRideProductAttribute),
      productVariation: new FormControl(productVariationAttributeTermRawValue.productVariation),
    });
  }

  getProductVariationAttributeTerm(
    form: ProductVariationAttributeTermFormGroup
  ): IProductVariationAttributeTerm | NewProductVariationAttributeTerm {
    return form.getRawValue() as IProductVariationAttributeTerm | NewProductVariationAttributeTerm;
  }

  resetForm(
    form: ProductVariationAttributeTermFormGroup,
    productVariationAttributeTerm: ProductVariationAttributeTermFormGroupInput
  ): void {
    const productVariationAttributeTermRawValue = { ...this.getFormDefaults(), ...productVariationAttributeTerm };
    form.reset(
      {
        ...productVariationAttributeTermRawValue,
        id: { value: productVariationAttributeTermRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductVariationAttributeTermFormDefaults {
    return {
      id: null,
      overRideProductAttribute: false,
    };
  }
}
