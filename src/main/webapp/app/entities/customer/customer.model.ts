import { IUser } from 'app/entities/user/user.model';
import { IAddress } from 'app/entities/address/address.model';

export interface ICustomer {
  id: number;
  name?: string | null;
  contactNumber?: string | null;
  avatarUrl?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  address?: Pick<IAddress, 'id' | 'name'> | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
