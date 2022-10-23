import { IProjectSpecificationGroup } from 'app/entities/project-specification-group/project-specification-group.model';
import { IProject } from 'app/entities/project/project.model';

export interface IProjectSpecification {
  id: number;
  title?: string | null;
  value?: string | null;
  description?: string | null;
  projectSpecificationGroup?: Pick<IProjectSpecificationGroup, 'id' | 'title'> | null;
  project?: Pick<IProject, 'id' | 'name'> | null;
}

export type NewProjectSpecification = Omit<IProjectSpecification, 'id'> & { id: null };
