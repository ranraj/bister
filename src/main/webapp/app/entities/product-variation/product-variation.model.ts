import dayjs from 'dayjs/esm';
import { IProduct } from 'app/entities/product/product.model';
import { SaleStatus } from 'app/entities/enumerations/sale-status.model';

export interface IProductVariation {
  id: number;
  assetId?: string | null;
  name?: string | null;
  description?: string | null;
  regularPrice?: number | null;
  salePrice?: number | null;
  dateOnSaleFrom?: dayjs.Dayjs | null;
  dateOnSaleTo?: dayjs.Dayjs | null;
  isDraft?: boolean | null;
  useParentDetails?: boolean | null;
  saleStatus?: SaleStatus | null;
  product?: Pick<IProduct, 'id' | 'name'> | null;
}

export type NewProductVariation = Omit<IProductVariation, 'id'> & { id: null };
