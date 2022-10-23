import { IAddress } from 'app/entities/address/address.model';
import { IBusinessPartner } from 'app/entities/business-partner/business-partner.model';

export interface IOrganisation {
  id: number;
  name?: string | null;
  description?: string | null;
  key?: string | null;
  address?: Pick<IAddress, 'id' | 'name'> | null;
  businessPartner?: Pick<IBusinessPartner, 'id' | 'name'> | null;
}

export type NewOrganisation = Omit<IOrganisation, 'id'> & { id: null };
