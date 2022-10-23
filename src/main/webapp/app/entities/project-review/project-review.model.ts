import { IProject } from 'app/entities/project/project.model';
import { ReviewStatus } from 'app/entities/enumerations/review-status.model';

export interface IProjectReview {
  id: number;
  reviewerName?: string | null;
  reviewerEmail?: string | null;
  review?: string | null;
  rating?: number | null;
  status?: ReviewStatus | null;
  reviewerId?: number | null;
  project?: Pick<IProject, 'id' | 'name'> | null;
}

export type NewProjectReview = Omit<IProjectReview, 'id'> & { id: null };
