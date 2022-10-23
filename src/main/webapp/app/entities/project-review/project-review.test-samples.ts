import { ReviewStatus } from 'app/entities/enumerations/review-status.model';

import { IProjectReview, NewProjectReview } from './project-review.model';

export const sampleWithRequiredData: IProjectReview = {
  id: 3421,
  reviewerName: 'Hat plug-and-play',
  reviewerEmail: '=@aDDF.y`t0}-',
  review: 'Account synthesizeXX',
  rating: 96356,
  status: ReviewStatus['UNSPAM'],
  reviewerId: 26266,
};

export const sampleWithPartialData: IProjectReview = {
  id: 94612,
  reviewerName: 'National',
  reviewerEmail: '3@!8zm9B.=;U',
  review: 'bandwidth Planner 24/7',
  rating: 27855,
  status: ReviewStatus['TRASH'],
  reviewerId: 98146,
};

export const sampleWithFullData: IProjectReview = {
  id: 31964,
  reviewerName: 'synthesizing',
  reviewerEmail: '{@IBza7{."S',
  review: 'GamesXXXXXXXXXXXXXXX',
  rating: 60890,
  status: ReviewStatus['UNSPAM'],
  reviewerId: 55533,
};

export const sampleWithNewData: NewProjectReview = {
  reviewerName: 'Markets PCI',
  reviewerEmail: 'XaNz@~_./"()C}',
  review: 'web-enabledXXXXXXXXX',
  rating: 89105,
  status: ReviewStatus['UNTRASH'],
  reviewerId: 51098,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
