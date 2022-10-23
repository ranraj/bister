export interface ITaxClass {
  id: number;
  name?: string | null;
  slug?: string | null;
}

export type NewTaxClass = Omit<ITaxClass, 'id'> & { id: null };
