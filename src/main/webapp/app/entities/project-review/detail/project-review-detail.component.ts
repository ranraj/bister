import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProjectReview } from '../project-review.model';

@Component({
  selector: 'yali-project-review-detail',
  templateUrl: './project-review-detail.component.html',
})
export class ProjectReviewDetailComponent implements OnInit {
  projectReview: IProjectReview | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectReview }) => {
      this.projectReview = projectReview;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
