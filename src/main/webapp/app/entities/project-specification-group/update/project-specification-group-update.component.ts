import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProjectSpecificationGroupFormService, ProjectSpecificationGroupFormGroup } from './project-specification-group-form.service';
import { IProjectSpecificationGroup } from '../project-specification-group.model';
import { ProjectSpecificationGroupService } from '../service/project-specification-group.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

@Component({
  selector: 'yali-project-specification-group-update',
  templateUrl: './project-specification-group-update.component.html',
})
export class ProjectSpecificationGroupUpdateComponent implements OnInit {
  isSaving = false;
  projectSpecificationGroup: IProjectSpecificationGroup | null = null;

  projectsSharedCollection: IProject[] = [];

  editForm: ProjectSpecificationGroupFormGroup = this.projectSpecificationGroupFormService.createProjectSpecificationGroupFormGroup();

  constructor(
    protected projectSpecificationGroupService: ProjectSpecificationGroupService,
    protected projectSpecificationGroupFormService: ProjectSpecificationGroupFormService,
    protected projectService: ProjectService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProject = (o1: IProject | null, o2: IProject | null): boolean => this.projectService.compareProject(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectSpecificationGroup }) => {
      this.projectSpecificationGroup = projectSpecificationGroup;
      if (projectSpecificationGroup) {
        this.updateForm(projectSpecificationGroup);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const projectSpecificationGroup = this.projectSpecificationGroupFormService.getProjectSpecificationGroup(this.editForm);
    if (projectSpecificationGroup.id !== null) {
      this.subscribeToSaveResponse(this.projectSpecificationGroupService.update(projectSpecificationGroup));
    } else {
      this.subscribeToSaveResponse(this.projectSpecificationGroupService.create(projectSpecificationGroup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProjectSpecificationGroup>>): void {
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

  protected updateForm(projectSpecificationGroup: IProjectSpecificationGroup): void {
    this.projectSpecificationGroup = projectSpecificationGroup;
    this.projectSpecificationGroupFormService.resetForm(this.editForm, projectSpecificationGroup);

    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing<IProject>(
      this.projectsSharedCollection,
      projectSpecificationGroup.project
    );
  }

  protected loadRelationshipsOptions(): void {
    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) =>
          this.projectService.addProjectToCollectionIfMissing<IProject>(projects, this.projectSpecificationGroup?.project)
        )
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));
  }
}
