import { IProject } from 'app/entities/project/project.model';

export interface IProjectSpecificationGroup {
  id: number;
  title?: string | null;
  slug?: string | null;
  description?: string | null;
  project?: Pick<IProject, 'id' | 'name'> | null;
}

export type NewProjectSpecificationGroup = Omit<IProjectSpecificationGroup, 'id'> & { id: null };
