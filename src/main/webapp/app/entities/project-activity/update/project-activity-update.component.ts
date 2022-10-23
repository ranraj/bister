import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProjectActivityFormService, ProjectActivityFormGroup } from './project-activity-form.service';
import { IProjectActivity } from '../project-activity.model';
import { ProjectActivityService } from '../service/project-activity.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { ActivityStatus } from 'app/entities/enumerations/activity-status.model';

@Component({
  selector: 'yali-project-activity-update',
  templateUrl: './project-activity-update.component.html',
})
export class ProjectActivityUpdateComponent implements OnInit {
  isSaving = false;
  projectActivity: IProjectActivity | null = null;
  activityStatusValues = Object.keys(ActivityStatus);

  projectsSharedCollection: IProject[] = [];

  editForm: ProjectActivityFormGroup = this.projectActivityFormService.createProjectActivityFormGroup();

  constructor(
    protected projectActivityService: ProjectActivityService,
    protected projectActivityFormService: ProjectActivityFormService,
    protected projectService: ProjectService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProject = (o1: IProject | null, o2: IProject | null): boolean => this.projectService.compareProject(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectActivity }) => {
      this.projectActivity = projectActivity;
      if (projectActivity) {
        this.updateForm(projectActivity);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const projectActivity = this.projectActivityFormService.getProjectActivity(this.editForm);
    if (projectActivity.id !== null) {
      this.subscribeToSaveResponse(this.projectActivityService.update(projectActivity));
    } else {
      this.subscribeToSaveResponse(this.projectActivityService.create(projectActivity));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProjectActivity>>): void {
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

  protected updateForm(projectActivity: IProjectActivity): void {
    this.projectActivity = projectActivity;
    this.projectActivityFormService.resetForm(this.editForm, projectActivity);

    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing<IProject>(
      this.projectsSharedCollection,
      projectActivity.project
    );
  }

  protected loadRelationshipsOptions(): void {
    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) =>
          this.projectService.addProjectToCollectionIfMissing<IProject>(projects, this.projectActivity?.project)
        )
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));
  }
}
