import { ITaxClass } from 'app/entities/tax-class/tax-class.model';

export interface ITaxRate {
  id: number;
  country?: string | null;
  state?: string | null;
  postcode?: string | null;
  city?: string | null;
  rate?: string | null;
  name?: string | null;
  compound?: boolean | null;
  priority?: number | null;
  taxClass?: Pick<ITaxClass, 'id' | 'name'> | null;
}

export type NewTaxRate = Omit<ITaxRate, 'id'> & { id: null };
