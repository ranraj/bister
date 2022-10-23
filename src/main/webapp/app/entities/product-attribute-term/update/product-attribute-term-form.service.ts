import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProductAttributeTerm, NewProductAttributeTerm } from '../product-attribute-term.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductAttributeTerm for edit and NewProductAttributeTermFormGroupInput for create.
 */
type ProductAttributeTermFormGroupInput = IProductAttributeTerm | PartialWithRequiredKeyOf<NewProductAttributeTerm>;

type ProductAttributeTermFormDefaults = Pick<NewProductAttributeTerm, 'id'>;

type ProductAttributeTermFormGroupContent = {
  id: FormControl<IProductAttributeTerm['id'] | NewProductAttributeTerm['id']>;
  name: FormControl<IProductAttributeTerm['name']>;
  slug: FormControl<IProductAttributeTerm['slug']>;
  description: FormControl<IProductAttributeTerm['description']>;
  menuOrder: FormControl<IProductAttributeTerm['menuOrder']>;
  productAttribute: FormControl<IProductAttributeTerm['productAttribute']>;
  product: FormControl<IProductAttributeTerm['product']>;
};

export type ProductAttributeTermFormGroup = FormGroup<ProductAttributeTermFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductAttributeTermFormService {
  createProductAttributeTermFormGroup(
    productAttributeTerm: ProductAttributeTermFormGroupInput = { id: null }
  ): ProductAttributeTermFormGroup {
    const productAttributeTermRawValue = {
      ...this.getFormDefaults(),
      ...productAttributeTerm,
    };
    return new FormGroup<ProductAttributeTermFormGroupContent>({
      id: new FormControl(
        { value: productAttributeTermRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(productAttributeTermRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      slug: new FormControl(productAttributeTermRawValue.slug, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(productAttributeTermRawValue.description, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(1000)],
      }),
      menuOrder: new FormControl(productAttributeTermRawValue.menuOrder, {
        validators: [Validators.required],
      }),
      productAttribute: new FormControl(productAttributeTermRawValue.productAttribute, {
        validators: [Validators.required],
      }),
      product: new FormControl(productAttributeTermRawValue.product, {
        validators: [Validators.required],
      }),
    });
  }

  getProductAttributeTerm(form: ProductAttributeTermFormGroup): IProductAttributeTerm | NewProductAttributeTerm {
    return form.getRawValue() as IProductAttributeTerm | NewProductAttributeTerm;
  }

  resetForm(form: ProductAttributeTermFormGroup, productAttributeTerm: ProductAttributeTermFormGroupInput): void {
    const productAttributeTermRawValue = { ...this.getFormDefaults(), ...productAttributeTerm };
    form.reset(
      {
        ...productAttributeTermRawValue,
        id: { value: productAttributeTermRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductAttributeTermFormDefaults {
    return {
      id: null,
    };
  }
}
