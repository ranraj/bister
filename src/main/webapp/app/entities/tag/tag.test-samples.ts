import { TagType } from 'app/entities/enumerations/tag-type.model';

import { ITag, NewTag } from './tag.model';

export const sampleWithRequiredData: ITag = {
  id: 42372,
  name: 'human-resource',
  slug: 'Principal HTTP Credit',
  description: 'intangibleXXXXXXXXXX',
  tagType: TagType['Project'],
};

export const sampleWithPartialData: ITag = {
  id: 54915,
  name: 'THX Burgs',
  slug: 'Fresh firmware Hampshire',
  description: 'architectures bus architectures',
  tagType: TagType['Project'],
};

export const sampleWithFullData: ITag = {
  id: 42709,
  name: 'XML needs-based Fundamental',
  slug: 'relationships envisioneer Lead',
  description: 'Concrete GamesXXXXXX',
  tagType: TagType['Attachment'],
};

export const sampleWithNewData: NewTag = {
  name: 'Rustic',
  slug: 'Cuba',
  description: 'indigoXXXXXXXXXXXXXX',
  tagType: TagType['Project'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
