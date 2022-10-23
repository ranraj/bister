import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ProjectTypeFormService, ProjectTypeFormGroup } from './project-type-form.service';
import { IProjectType } from '../project-type.model';
import { ProjectTypeService } from '../service/project-type.service';

@Component({
  selector: 'yali-project-type-update',
  templateUrl: './project-type-update.component.html',
})
export class ProjectTypeUpdateComponent implements OnInit {
  isSaving = false;
  projectType: IProjectType | null = null;

  editForm: ProjectTypeFormGroup = this.projectTypeFormService.createProjectTypeFormGroup();

  constructor(
    protected projectTypeService: ProjectTypeService,
    protected projectTypeFormService: ProjectTypeFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projectType }) => {
      this.projectType = projectType;
      if (projectType) {
        this.updateForm(projectType);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const projectType = this.projectTypeFormService.getProjectType(this.editForm);
    if (projectType.id !== null) {
      this.subscribeToSaveResponse(this.projectTypeService.update(projectType));
    } else {
      this.subscribeToSaveResponse(this.projectTypeService.create(projectType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProjectType>>): void {
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

  protected updateForm(projectType: IProjectType): void {
    this.projectType = projectType;
    this.projectTypeFormService.resetForm(this.editForm, projectType);
  }
}
