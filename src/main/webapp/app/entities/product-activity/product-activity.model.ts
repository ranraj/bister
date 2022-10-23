import { IProduct } from 'app/entities/product/product.model';
import { ActivityStatus } from 'app/entities/enumerations/activity-status.model';

export interface IProductActivity {
  id: number;
  title?: string | null;
  details?: string | null;
  status?: ActivityStatus | null;
  product?: Pick<IProduct, 'id' | 'name'> | null;
}

export type NewProductActivity = Omit<IProductActivity, 'id'> & { id: null };
