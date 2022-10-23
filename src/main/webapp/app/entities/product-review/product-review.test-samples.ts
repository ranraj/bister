import { ReviewStatus } from 'app/entities/enumerations/review-status.model';

import { IProductReview, NewProductReview } from './product-review.model';

export const sampleWithRequiredData: IProductReview = {
  id: 8901,
  reviewerName: 'Incredible',
  reviewerEmail: '$}R@>[#&3%.FN',
  review: 'Savings',
  rating: 98123,
  status: ReviewStatus['APPROVED'],
  reviewerId: 43567,
};

export const sampleWithPartialData: IProductReview = {
  id: 46938,
  reviewerName: 'Unbranded',
  reviewerEmail: 'M`oX@x.}',
  review: 'Table Andorra',
  rating: 38477,
  status: ReviewStatus['UNSPAM'],
  reviewerId: 50283,
};

export const sampleWithFullData: IProductReview = {
  id: 87649,
  reviewerName: 'integrate initiatives',
  reviewerEmail: '$lKb@p7u.Rugp',
  review: 'info-mediaries',
  rating: 31343,
  status: ReviewStatus['UNTRASH'],
  reviewerId: 7624,
};

export const sampleWithNewData: NewProductReview = {
  reviewerName: 'Roads',
  reviewerEmail: '<O|K@%<03.<T74',
  review: 'generation Account architectures',
  rating: 60332,
  status: ReviewStatus['UNTRASH'],
  reviewerId: 15464,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
