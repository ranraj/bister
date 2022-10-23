export interface IProductAttribute {
  id: number;
  name?: string | null;
  slug?: string | null;
  type?: string | null;
  notes?: string | null;
  visible?: boolean | null;
}

export type NewProductAttribute = Omit<IProductAttribute, 'id'> & { id: null };
