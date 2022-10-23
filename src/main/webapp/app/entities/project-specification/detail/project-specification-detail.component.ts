import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProjectSpecification } from '../project-specification.model';

@Component({
  selector: 'yali-project-specification-detail',
  templateUrl: './project-specification-detail.component.html',
})
export class ProjectSpecificationDetailComponent implements OnInit {
  projectSpecification: IProjectSpecification | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectSpecification }) => {
      this.projectSpecification = projectSpecification;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
