import { IProduct } from 'app/entities/product/product.model';

export interface IProductSpecificationGroup {
  id: number;
  title?: string | null;
  slug?: string | null;
  description?: string | null;
  product?: Pick<IProduct, 'id' | 'name'> | null;
}

export type NewProductSpecificationGroup = Omit<IProductSpecificationGroup, 'id'> & { id: null };
