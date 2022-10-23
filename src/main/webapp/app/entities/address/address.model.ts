import { AddressType } from 'app/entities/enumerations/address-type.model';

export interface IAddress {
  id: number;
  name?: string | null;
  addressLine1?: string | null;
  addressLine2?: string | null;
  landmark?: string | null;
  city?: string | null;
  state?: string | null;
  country?: string | null;
  postcode?: string | null;
  latitude?: string | null;
  longitude?: string | null;
  addressType?: AddressType | null;
}

export type NewAddress = Omit<IAddress, 'id'> & { id: null };
