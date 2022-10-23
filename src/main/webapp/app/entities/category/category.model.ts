import { IProduct } from 'app/entities/product/product.model';
import { IProject } from 'app/entities/project/project.model';
import { CategoryType } from 'app/entities/enumerations/category-type.model';

export interface ICategory {
  id: number;
  name?: string | null;
  slug?: string | null;
  description?: string | null;
  categoryType?: CategoryType | null;
  parent?: Pick<ICategory, 'id'> | null;
  products?: Pick<IProduct, 'id' | 'name'>[] | null;
  projects?: Pick<IProject, 'id' | 'name'>[] | null;
}

export type NewCategory = Omit<ICategory, 'id'> & { id: null };
