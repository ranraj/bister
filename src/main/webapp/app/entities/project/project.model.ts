import dayjs from 'dayjs/esm';
import { IAddress } from 'app/entities/address/address.model';
import { IProjectType } from 'app/entities/project-type/project-type.model';
import { ICategory } from 'app/entities/category/category.model';
import { ProjectStatus } from 'app/entities/enumerations/project-status.model';

export interface IProject {
  id: number;
  name?: string | null;
  slug?: string | null;
  description?: string | null;
  shortDescription?: string | null;
  regularPrice?: number | null;
  salePrice?: number | null;
  published?: boolean | null;
  dateCreated?: dayjs.Dayjs | null;
  dateModified?: dayjs.Dayjs | null;
  projectStatus?: ProjectStatus | null;
  sharableHash?: string | null;
  estimatedBudget?: number | null;
  address?: Pick<IAddress, 'id' | 'name'> | null;
  projectType?: Pick<IProjectType, 'id' | 'name'> | null;
  categories?: Pick<ICategory, 'id' | 'name'>[] | null;
}

export type NewProject = Omit<IProject, 'id'> & { id: null };
