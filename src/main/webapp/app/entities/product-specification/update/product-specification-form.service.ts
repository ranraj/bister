import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProductSpecification, NewProductSpecification } from '../product-specification.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductSpecification for edit and NewProductSpecificationFormGroupInput for create.
 */
type ProductSpecificationFormGroupInput = IProductSpecification | PartialWithRequiredKeyOf<NewProductSpecification>;

type ProductSpecificationFormDefaults = Pick<NewProductSpecification, 'id'>;

type ProductSpecificationFormGroupContent = {
  id: FormControl<IProductSpecification['id'] | NewProductSpecification['id']>;
  title: FormControl<IProductSpecification['title']>;
  value: FormControl<IProductSpecification['value']>;
  description: FormControl<IProductSpecification['description']>;
  productSpecificationGroup: FormControl<IProductSpecification['productSpecificationGroup']>;
  product: FormControl<IProductSpecification['product']>;
};

export type ProductSpecificationFormGroup = FormGroup<ProductSpecificationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductSpecificationFormService {
  createProductSpecificationFormGroup(
    productSpecification: ProductSpecificationFormGroupInput = { id: null }
  ): ProductSpecificationFormGroup {
    const productSpecificationRawValue = {
      ...this.getFormDefaults(),
      ...productSpecification,
    };
    return new FormGroup<ProductSpecificationFormGroupContent>({
      id: new FormControl(
        { value: productSpecificationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(productSpecificationRawValue.title, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100)],
      }),
      value: new FormControl(productSpecificationRawValue.value, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(productSpecificationRawValue.description, {
        validators: [Validators.minLength(20), Validators.maxLength(250)],
      }),
      productSpecificationGroup: new FormControl(productSpecificationRawValue.productSpecificationGroup),
      product: new FormControl(productSpecificationRawValue.product, {
        validators: [Validators.required],
      }),
    });
  }

  getProductSpecification(form: ProductSpecificationFormGroup): IProductSpecification | NewProductSpecification {
    return form.getRawValue() as IProductSpecification | NewProductSpecification;
  }

  resetForm(form: ProductSpecificationFormGroup, productSpecification: ProductSpecificationFormGroupInput): void {
    const productSpecificationRawValue = { ...this.getFormDefaults(), ...productSpecification };
    form.reset(
      {
        ...productSpecificationRawValue,
        id: { value: productSpecificationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductSpecificationFormDefaults {
    return {
      id: null,
    };
  }
}
