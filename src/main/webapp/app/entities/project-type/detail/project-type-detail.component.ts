import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProjectType } from '../project-type.model';

@Component({
  selector: 'yali-project-type-detail',
  templateUrl: './project-type-detail.component.html',
})
export class ProjectTypeDetailComponent implements OnInit {
  projectType: IProjectType | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectType }) => {
      this.projectType = projectType;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
