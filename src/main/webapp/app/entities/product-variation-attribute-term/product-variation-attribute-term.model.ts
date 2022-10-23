import { IProductVariation } from 'app/entities/product-variation/product-variation.model';

export interface IProductVariationAttributeTerm {
  id: number;
  name?: string | null;
  slug?: string | null;
  description?: string | null;
  menuOrder?: number | null;
  overRideProductAttribute?: boolean | null;
  productVariation?: Pick<IProductVariation, 'id' | 'name'> | null;
}

export type NewProductVariationAttributeTerm = Omit<IProductVariationAttributeTerm, 'id'> & { id: null };
