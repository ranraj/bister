export interface IBusinessPartner {
  id: number;
  name?: string | null;
  description?: string | null;
  key?: string | null;
}

export type NewBusinessPartner = Omit<IBusinessPartner, 'id'> & { id: null };
