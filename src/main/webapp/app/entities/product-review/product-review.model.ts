import { IProduct } from 'app/entities/product/product.model';
import { ReviewStatus } from 'app/entities/enumerations/review-status.model';

export interface IProductReview {
  id: number;
  reviewerName?: string | null;
  reviewerEmail?: string | null;
  review?: string | null;
  rating?: number | null;
  status?: ReviewStatus | null;
  reviewerId?: number | null;
  product?: Pick<IProduct, 'id' | 'name'> | null;
}

export type NewProductReview = Omit<IProductReview, 'id'> & { id: null };
