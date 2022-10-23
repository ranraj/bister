import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProductSpecificationGroup, NewProductSpecificationGroup } from '../product-specification-group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductSpecificationGroup for edit and NewProductSpecificationGroupFormGroupInput for create.
 */
type ProductSpecificationGroupFormGroupInput = IProductSpecificationGroup | PartialWithRequiredKeyOf<NewProductSpecificationGroup>;

type ProductSpecificationGroupFormDefaults = Pick<NewProductSpecificationGroup, 'id'>;

type ProductSpecificationGroupFormGroupContent = {
  id: FormControl<IProductSpecificationGroup['id'] | NewProductSpecificationGroup['id']>;
  title: FormControl<IProductSpecificationGroup['title']>;
  slug: FormControl<IProductSpecificationGroup['slug']>;
  description: FormControl<IProductSpecificationGroup['description']>;
  product: FormControl<IProductSpecificationGroup['product']>;
};

export type ProductSpecificationGroupFormGroup = FormGroup<ProductSpecificationGroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductSpecificationGroupFormService {
  createProductSpecificationGroupFormGroup(
    productSpecificationGroup: ProductSpecificationGroupFormGroupInput = { id: null }
  ): ProductSpecificationGroupFormGroup {
    const productSpecificationGroupRawValue = {
      ...this.getFormDefaults(),
      ...productSpecificationGroup,
    };
    return new FormGroup<ProductSpecificationGroupFormGroupContent>({
      id: new FormControl(
        { value: productSpecificationGroupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(productSpecificationGroupRawValue.title, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(100)],
      }),
      slug: new FormControl(productSpecificationGroupRawValue.slug, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(productSpecificationGroupRawValue.description, {
        validators: [Validators.minLength(20), Validators.maxLength(250)],
      }),
      product: new FormControl(productSpecificationGroupRawValue.product),
    });
  }

  getProductSpecificationGroup(form: ProductSpecificationGroupFormGroup): IProductSpecificationGroup | NewProductSpecificationGroup {
    return form.getRawValue() as IProductSpecificationGroup | NewProductSpecificationGroup;
  }

  resetForm(form: ProductSpecificationGroupFormGroup, productSpecificationGroup: ProductSpecificationGroupFormGroupInput): void {
    const productSpecificationGroupRawValue = { ...this.getFormDefaults(), ...productSpecificationGroup };
    form.reset(
      {
        ...productSpecificationGroupRawValue,
        id: { value: productSpecificationGroupRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductSpecificationGroupFormDefaults {
    return {
      id: null,
    };
  }
}
