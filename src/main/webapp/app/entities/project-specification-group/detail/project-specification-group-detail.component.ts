import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProjectSpecificationGroup } from '../project-specification-group.model';

@Component({
  selector: 'yali-project-specification-group-detail',
  templateUrl: './project-specification-group-detail.component.html',
})
export class ProjectSpecificationGroupDetailComponent implements OnInit {
  projectSpecificationGroup: IProjectSpecificationGroup | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectSpecificationGroup }) => {
      this.projectSpecificationGroup = projectSpecificationGroup;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
