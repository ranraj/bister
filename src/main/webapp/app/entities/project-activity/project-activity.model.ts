import { IProject } from 'app/entities/project/project.model';
import { ActivityStatus } from 'app/entities/enumerations/activity-status.model';

export interface IProjectActivity {
  id: number;
  title?: string | null;
  details?: string | null;
  status?: ActivityStatus | null;
  project?: Pick<IProject, 'id' | 'name'> | null;
}

export type NewProjectActivity = Omit<IProjectActivity, 'id'> & { id: null };
