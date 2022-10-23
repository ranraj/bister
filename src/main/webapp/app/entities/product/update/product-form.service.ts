import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProduct, NewProduct } from '../product.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProduct for edit and NewProductFormGroupInput for create.
 */
type ProductFormGroupInput = IProduct | PartialWithRequiredKeyOf<NewProduct>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProduct | NewProduct> = Omit<T, 'dateCreated'> & {
  dateCreated?: string | null;
};

type ProductFormRawValue = FormValueOf<IProduct>;

type NewProductFormRawValue = FormValueOf<NewProduct>;

type ProductFormDefaults = Pick<NewProduct, 'id' | 'published' | 'dateCreated' | 'featured' | 'categories'>;

type ProductFormGroupContent = {
  id: FormControl<ProductFormRawValue['id'] | NewProduct['id']>;
  name: FormControl<ProductFormRawValue['name']>;
  slug: FormControl<ProductFormRawValue['slug']>;
  description: FormControl<ProductFormRawValue['description']>;
  shortDescription: FormControl<ProductFormRawValue['shortDescription']>;
  regularPrice: FormControl<ProductFormRawValue['regularPrice']>;
  salePrice: FormControl<ProductFormRawValue['salePrice']>;
  published: FormControl<ProductFormRawValue['published']>;
  dateCreated: FormControl<ProductFormRawValue['dateCreated']>;
  dateModified: FormControl<ProductFormRawValue['dateModified']>;
  featured: FormControl<ProductFormRawValue['featured']>;
  saleStatus: FormControl<ProductFormRawValue['saleStatus']>;
  sharableHash: FormControl<ProductFormRawValue['sharableHash']>;
  project: FormControl<ProductFormRawValue['project']>;
  categories: FormControl<ProductFormRawValue['categories']>;
  taxClass: FormControl<ProductFormRawValue['taxClass']>;
};

export type ProductFormGroup = FormGroup<ProductFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductFormService {
  createProductFormGroup(product: ProductFormGroupInput = { id: null }): ProductFormGroup {
    const productRawValue = this.convertProductToProductRawValue({
      ...this.getFormDefaults(),
      ...product,
    });
    return new FormGroup<ProductFormGroupContent>({
      id: new FormControl(
        { value: productRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(productRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(250)],
      }),
      slug: new FormControl(productRawValue.slug, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(productRawValue.description, {
        validators: [Validators.required, Validators.minLength(20), Validators.maxLength(1000)],
      }),
      shortDescription: new FormControl(productRawValue.shortDescription, {
        validators: [Validators.required, Validators.minLength(20), Validators.maxLength(50)],
      }),
      regularPrice: new FormControl(productRawValue.regularPrice, {
        validators: [Validators.required],
      }),
      salePrice: new FormControl(productRawValue.salePrice, {
        validators: [Validators.required],
      }),
      published: new FormControl(productRawValue.published, {
        validators: [Validators.required],
      }),
      dateCreated: new FormControl(productRawValue.dateCreated, {
        validators: [Validators.required],
      }),
      dateModified: new FormControl(productRawValue.dateModified, {
        validators: [Validators.required],
      }),
      featured: new FormControl(productRawValue.featured, {
        validators: [Validators.required],
      }),
      saleStatus: new FormControl(productRawValue.saleStatus, {
        validators: [Validators.required],
      }),
      sharableHash: new FormControl(productRawValue.sharableHash),
      project: new FormControl(productRawValue.project),
      categories: new FormControl(productRawValue.categories ?? []),
      taxClass: new FormControl(productRawValue.taxClass, {
        validators: [Validators.required],
      }),
    });
  }

  getProduct(form: ProductFormGroup): IProduct | NewProduct {
    return this.convertProductRawValueToProduct(form.getRawValue() as ProductFormRawValue | NewProductFormRawValue);
  }

  resetForm(form: ProductFormGroup, product: ProductFormGroupInput): void {
    const productRawValue = this.convertProductToProductRawValue({ ...this.getFormDefaults(), ...product });
    form.reset(
      {
        ...productRawValue,
        id: { value: productRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      published: false,
      dateCreated: currentTime,
      featured: false,
      categories: [],
    };
  }

  private convertProductRawValueToProduct(rawProduct: ProductFormRawValue | NewProductFormRawValue): IProduct | NewProduct {
    return {
      ...rawProduct,
      dateCreated: dayjs(rawProduct.dateCreated, DATE_TIME_FORMAT),
    };
  }

  private convertProductToProductRawValue(
    product: IProduct | (Partial<NewProduct> & ProductFormDefaults)
  ): ProductFormRawValue | PartialWithRequiredKeyOf<NewProductFormRawValue> {
    return {
      ...product,
      dateCreated: product.dateCreated ? product.dateCreated.format(DATE_TIME_FORMAT) : undefined,
      categories: product.categories ?? [],
    };
  }
}
