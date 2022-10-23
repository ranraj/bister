import { IAddress } from 'app/entities/address/address.model';
import { IUser } from 'app/entities/user/user.model';
import { IOrganisation } from 'app/entities/organisation/organisation.model';
import { FacilityType } from 'app/entities/enumerations/facility-type.model';

export interface IFacility {
  id: number;
  name?: string | null;
  description?: string | null;
  facilityType?: FacilityType | null;
  address?: Pick<IAddress, 'id' | 'name'> | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  organisation?: Pick<IOrganisation, 'id' | 'name'> | null;
}

export type NewFacility = Omit<IFacility, 'id'> & { id: null };
