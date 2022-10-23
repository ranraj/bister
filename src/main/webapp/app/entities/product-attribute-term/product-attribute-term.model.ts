import { IProductAttribute } from 'app/entities/product-attribute/product-attribute.model';
import { IProduct } from 'app/entities/product/product.model';

export interface IProductAttributeTerm {
  id: number;
  name?: string | null;
  slug?: string | null;
  description?: string | null;
  menuOrder?: number | null;
  productAttribute?: Pick<IProductAttribute, 'id' | 'name'> | null;
  product?: Pick<IProduct, 'id' | 'name'> | null;
}

export type NewProductAttributeTerm = Omit<IProductAttributeTerm, 'id'> & { id: null };
