export interface IProjectType {
  id: number;
  name?: string | null;
  description?: string | null;
}

export type NewProjectType = Omit<IProjectType, 'id'> & { id: null };
