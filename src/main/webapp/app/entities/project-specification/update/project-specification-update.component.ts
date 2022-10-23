import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProjectSpecificationFormService, ProjectSpecificationFormGroup } from './project-specification-form.service';
import { IProjectSpecification } from '../project-specification.model';
import { ProjectSpecificationService } from '../service/project-specification.service';
import { IProjectSpecificationGroup } from 'app/entities/project-specification-group/project-specification-group.model';
import { ProjectSpecificationGroupService } from 'app/entities/project-specification-group/service/project-specification-group.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

@Component({
  selector: 'yali-project-specification-update',
  templateUrl: './project-specification-update.component.html',
})
export class ProjectSpecificationUpdateComponent implements OnInit {
  isSaving = false;
  projectSpecification: IProjectSpecification | null = null;

  projectSpecificationGroupsSharedCollection: IProjectSpecificationGroup[] = [];
  projectsSharedCollection: IProject[] = [];

  editForm: ProjectSpecificationFormGroup = this.projectSpecificationFormService.createProjectSpecificationFormGroup();

  constructor(
    protected projectSpecificationService: ProjectSpecificationService,
    protected projectSpecificationFormService: ProjectSpecificationFormService,
    protected projectSpecificationGroupService: ProjectSpecificationGroupService,
    protected projectService: ProjectService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProjectSpecificationGroup = (o1: IProjectSpecificationGroup | null, o2: IProjectSpecificationGroup | null): boolean =>
    this.projectSpecificationGroupService.compareProjectSpecificationGroup(o1, o2);

  compareProject = (o1: IProject | null, o2: IProject | null): boolean => this.projectService.compareProject(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectSpecification }) => {
      this.projectSpecification = projectSpecification;
      if (projectSpecification) {
        this.updateForm(projectSpecification);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const projectSpecification = this.projectSpecificationFormService.getProjectSpecification(this.editForm);
    if (projectSpecification.id !== null) {
      this.subscribeToSaveResponse(this.projectSpecificationService.update(projectSpecification));
    } else {
      this.subscribeToSaveResponse(this.projectSpecificationService.create(projectSpecification));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProjectSpecification>>): void {
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

  protected updateForm(projectSpecification: IProjectSpecification): void {
    this.projectSpecification = projectSpecification;
    this.projectSpecificationFormService.resetForm(this.editForm, projectSpecification);

    this.projectSpecificationGroupsSharedCollection =
      this.projectSpecificationGroupService.addProjectSpecificationGroupToCollectionIfMissing<IProjectSpecificationGroup>(
        this.projectSpecificationGroupsSharedCollection,
        projectSpecification.projectSpecificationGroup
      );
    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing<IProject>(
      this.projectsSharedCollection,
      projectSpecification.project
    );
  }

  protected loadRelationshipsOptions(): void {
    this.projectSpecificationGroupService
      .query()
      .pipe(map((res: HttpResponse<IProjectSpecificationGroup[]>) => res.body ?? []))
      .pipe(
        map((projectSpecificationGroups: IProjectSpecificationGroup[]) =>
          this.projectSpecificationGroupService.addProjectSpecificationGroupToCollectionIfMissing<IProjectSpecificationGroup>(
            projectSpecificationGroups,
            this.projectSpecification?.projectSpecificationGroup
          )
        )
      )
      .subscribe(
        (projectSpecificationGroups: IProjectSpecificationGroup[]) =>
          (this.projectSpecificationGroupsSharedCollection = projectSpecificationGroups)
      );

    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) =>
          this.projectService.addProjectToCollectionIfMissing<IProject>(projects, this.projectSpecification?.project)
        )
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));
  }
}
