import { IProductSpecificationGroup } from 'app/entities/product-specification-group/product-specification-group.model';
import { IProduct } from 'app/entities/product/product.model';

export interface IProductSpecification {
  id: number;
  title?: string | null;
  value?: string | null;
  description?: string | null;
  productSpecificationGroup?: Pick<IProductSpecificationGroup, 'id' | 'title'> | null;
  product?: Pick<IProduct, 'id' | 'name'> | null;
}

export type NewProductSpecification = Omit<IProductSpecification, 'id'> & { id: null };
