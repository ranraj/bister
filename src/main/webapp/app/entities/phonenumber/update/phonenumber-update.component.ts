import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PhonenumberFormService, PhonenumberFormGroup } from './phonenumber-form.service';
import { IPhonenumber } from '../phonenumber.model';
import { PhonenumberService } from '../service/phonenumber.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisation } from 'app/entities/organisation/organisation.model';
import { OrganisationService } from 'app/entities/organisation/service/organisation.service';
import { IFacility } from 'app/entities/facility/facility.model';
import { FacilityService } from 'app/entities/facility/service/facility.service';
import { PhonenumberType } from 'app/entities/enumerations/phonenumber-type.model';

@Component({
  selector: 'yali-phonenumber-update',
  templateUrl: './phonenumber-update.component.html',
})
export class PhonenumberUpdateComponent implements OnInit {
  isSaving = false;
  phonenumber: IPhonenumber | null = null;
  phonenumberTypeValues = Object.keys(PhonenumberType);

  usersSharedCollection: IUser[] = [];
  organisationsSharedCollection: IOrganisation[] = [];
  facilitiesSharedCollection: IFacility[] = [];

  editForm: PhonenumberFormGroup = this.phonenumberFormService.createPhonenumberFormGroup();

  constructor(
    protected phonenumberService: PhonenumberService,
    protected phonenumberFormService: PhonenumberFormService,
    protected userService: UserService,
    protected organisationService: OrganisationService,
    protected facilityService: FacilityService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareOrganisation = (o1: IOrganisation | null, o2: IOrganisation | null): boolean =>
    this.organisationService.compareOrganisation(o1, o2);

  compareFacility = (o1: IFacility | null, o2: IFacility | null): boolean => this.facilityService.compareFacility(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ phonenumber }) => {
      this.phonenumber = phonenumber;
      if (phonenumber) {
        this.updateForm(phonenumber);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const phonenumber = this.phonenumberFormService.getPhonenumber(this.editForm);
    if (phonenumber.id !== null) {
      this.subscribeToSaveResponse(this.phonenumberService.update(phonenumber));
    } else {
      this.subscribeToSaveResponse(this.phonenumberService.create(phonenumber));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPhonenumber>>): void {
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

  protected updateForm(phonenumber: IPhonenumber): void {
    this.phonenumber = phonenumber;
    this.phonenumberFormService.resetForm(this.editForm, phonenumber);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, phonenumber.user);
    this.organisationsSharedCollection = this.organisationService.addOrganisationToCollectionIfMissing<IOrganisation>(
      this.organisationsSharedCollection,
      phonenumber.organisation
    );
    this.facilitiesSharedCollection = this.facilityService.addFacilityToCollectionIfMissing<IFacility>(
      this.facilitiesSharedCollection,
      phonenumber.facility
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.phonenumber?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.organisationService
      .query()
      .pipe(map((res: HttpResponse<IOrganisation[]>) => res.body ?? []))
      .pipe(
        map((organisations: IOrganisation[]) =>
          this.organisationService.addOrganisationToCollectionIfMissing<IOrganisation>(organisations, this.phonenumber?.organisation)
        )
      )
      .subscribe((organisations: IOrganisation[]) => (this.organisationsSharedCollection = organisations));

    this.facilityService
      .query()
      .pipe(map((res: HttpResponse<IFacility[]>) => res.body ?? []))
      .pipe(
        map((facilities: IFacility[]) =>
          this.facilityService.addFacilityToCollectionIfMissing<IFacility>(facilities, this.phonenumber?.facility)
        )
      )
      .subscribe((facilities: IFacility[]) => (this.facilitiesSharedCollection = facilities));
  }
}
