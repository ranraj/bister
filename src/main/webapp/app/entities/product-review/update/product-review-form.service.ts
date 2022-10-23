import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProductReview, NewProductReview } from '../product-review.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductReview for edit and NewProductReviewFormGroupInput for create.
 */
type ProductReviewFormGroupInput = IProductReview | PartialWithRequiredKeyOf<NewProductReview>;

type ProductReviewFormDefaults = Pick<NewProductReview, 'id'>;

type ProductReviewFormGroupContent = {
  id: FormControl<IProductReview['id'] | NewProductReview['id']>;
  reviewerName: FormControl<IProductReview['reviewerName']>;
  reviewerEmail: FormControl<IProductReview['reviewerEmail']>;
  review: FormControl<IProductReview['review']>;
  rating: FormControl<IProductReview['rating']>;
  status: FormControl<IProductReview['status']>;
  reviewerId: FormControl<IProductReview['reviewerId']>;
  product: FormControl<IProductReview['product']>;
};

export type ProductReviewFormGroup = FormGroup<ProductReviewFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductReviewFormService {
  createProductReviewFormGroup(productReview: ProductReviewFormGroupInput = { id: null }): ProductReviewFormGroup {
    const productReviewRawValue = {
      ...this.getFormDefaults(),
      ...productReview,
    };
    return new FormGroup<ProductReviewFormGroupContent>({
      id: new FormControl(
        { value: productReviewRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      reviewerName: new FormControl(productReviewRawValue.reviewerName, {
        validators: [Validators.required, Validators.minLength(1), Validators.maxLength(250)],
      }),
      reviewerEmail: new FormControl(productReviewRawValue.reviewerEmail, {
        validators: [Validators.required, Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$')],
      }),
      review: new FormControl(productReviewRawValue.review, {
        validators: [Validators.required, Validators.minLength(5), Validators.maxLength(1000)],
      }),
      rating: new FormControl(productReviewRawValue.rating, {
        validators: [Validators.required],
      }),
      status: new FormControl(productReviewRawValue.status, {
        validators: [Validators.required],
      }),
      reviewerId: new FormControl(productReviewRawValue.reviewerId, {
        validators: [Validators.required],
      }),
      product: new FormControl(productReviewRawValue.product, {
        validators: [Validators.required],
      }),
    });
  }

  getProductReview(form: ProductReviewFormGroup): IProductReview | NewProductReview {
    return form.getRawValue() as IProductReview | NewProductReview;
  }

  resetForm(form: ProductReviewFormGroup, productReview: ProductReviewFormGroupInput): void {
    const productReviewRawValue = { ...this.getFormDefaults(), ...productReview };
    form.reset(
      {
        ...productReviewRawValue,
        id: { value: productReviewRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductReviewFormDefaults {
    return {
      id: null,
    };
  }
}
