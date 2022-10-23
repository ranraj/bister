import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProjectActivity } from '../project-activity.model';

@Component({
  selector: 'yali-project-activity-detail',
  templateUrl: './project-activity-detail.component.html',
})
export class ProjectActivityDetailComponent implements OnInit {
  projectActivity: IProjectActivity | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectActivity }) => {
      this.projectActivity = projectActivity;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
