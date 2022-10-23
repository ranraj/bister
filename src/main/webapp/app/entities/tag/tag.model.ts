import { IProduct } from 'app/entities/product/product.model';
import { IProject } from 'app/entities/project/project.model';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { TagType } from 'app/entities/enumerations/tag-type.model';

export interface ITag {
  id: number;
  name?: string | null;
  slug?: string | null;
  description?: string | null;
  tagType?: TagType | null;
  product?: Pick<IProduct, 'id' | 'name'> | null;
  project?: Pick<IProject, 'id' | 'name'> | null;
  attachment?: Pick<IAttachment, 'id' | 'name'> | null;
}

export type NewTag = Omit<ITag, 'id'> & { id: null };
