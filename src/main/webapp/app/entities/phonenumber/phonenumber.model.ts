import { IUser } from 'app/entities/user/user.model';
import { IOrganisation } from 'app/entities/organisation/organisation.model';
import { IFacility } from 'app/entities/facility/facility.model';
import { PhonenumberType } from 'app/entities/enumerations/phonenumber-type.model';

export interface IPhonenumber {
  id: number;
  country?: string | null;
  code?: string | null;
  contactNumber?: string | null;
  phonenumberType?: PhonenumberType | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  organisation?: Pick<IOrganisation, 'id' | 'name'> | null;
  facility?: Pick<IFacility, 'id' | 'name'> | null;
}

export type NewPhonenumber = Omit<IPhonenumber, 'id'> & { id: null };
