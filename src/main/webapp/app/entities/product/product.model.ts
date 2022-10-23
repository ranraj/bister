import dayjs from 'dayjs/esm';
import { IProject } from 'app/entities/project/project.model';
import { ICategory } from 'app/entities/category/category.model';
import { ITaxClass } from 'app/entities/tax-class/tax-class.model';
import { SaleStatus } from 'app/entities/enumerations/sale-status.model';

export interface IProduct {
  id: number;
  name?: string | null;
  slug?: string | null;
  description?: string | null;
  shortDescription?: string | null;
  regularPrice?: number | null;
  salePrice?: number | null;
  published?: boolean | null;
  dateCreated?: dayjs.Dayjs | null;
  dateModified?: dayjs.Dayjs | null;
  featured?: boolean | null;
  saleStatus?: SaleStatus | null;
  sharableHash?: string | null;
  project?: Pick<IProject, 'id' | 'name'> | null;
  categories?: Pick<ICategory, 'id' | 'name'>[] | null;
  taxClass?: Pick<ITaxClass, 'id' | 'name'> | null;
}

export type NewProduct = Omit<IProduct, 'id'> & { id: null };
