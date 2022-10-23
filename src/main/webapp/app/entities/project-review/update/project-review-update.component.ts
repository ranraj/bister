import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProjectReviewFormService, ProjectReviewFormGroup } from './project-review-form.service';
import { IProjectReview } from '../project-review.model';
import { ProjectReviewService } from '../service/project-review.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { ReviewStatus } from 'app/entities/enumerations/review-status.model';

@Component({
  selector: 'yali-project-review-update',
  templateUrl: './project-review-update.component.html',
})
export class ProjectReviewUpdateComponent implements OnInit {
  isSaving = false;
  projectReview: IProjectReview | null = null;
  reviewStatusValues = Object.keys(ReviewStatus);

  projectsSharedCollection: IProject[] = [];

  editForm: ProjectReviewFormGroup = this.projectReviewFormService.createProjectReviewFormGroup();

  constructor(
    protected projectReviewService: ProjectReviewService,
    protected projectReviewFormService: ProjectReviewFormService,
    protected projectService: ProjectService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProject = (o1: IProject | null, o2: IProject | null): boolean => this.projectService.compareProject(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectReview }) => {
      this.projectReview = projectReview;
      if (projectReview) {
        this.updateForm(projectReview);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const projectReview = this.projectReviewFormService.getProjectReview(this.editForm);
    if (projectReview.id !== null) {
      this.subscribeToSaveResponse(this.projectReviewService.update(projectReview));
    } else {
      this.subscribeToSaveResponse(this.projectReviewService.create(projectReview));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProjectReview>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(projectReview: IProjectReview): void {
    this.projectReview = projectReview;
    this.projectReviewFormService.resetForm(this.editForm, projectReview);

    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing<IProject>(
      this.projectsSharedCollection,
      projectReview.project
    );
  }

  protected loadRelationshipsOptions(): void {
    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) => this.projectService.addProjectToCollectionIfMissing<IProject>(projects, this.projectReview?.project))
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));
  }
}
