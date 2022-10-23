import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PhonenumberFormService } from './phonenumber-form.service';
import { PhonenumberService } from '../service/phonenumber.service';
import { IPhonenumber } from '../phonenumber.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisation } from 'app/entities/organisation/organisation.model';
import { OrganisationService } from 'app/entities/organisation/service/organisation.service';
import { IFacility } from 'app/entities/facility/facility.model';
import { FacilityService } from 'app/entities/facility/service/facility.service';

import { PhonenumberUpdateComponent } from './phonenumber-update.component';

describe('Phonenumber Management Update Component', () => {
  let comp: PhonenumberUpdateComponent;
  let fixture: ComponentFixture<PhonenumberUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let phonenumberFormService: PhonenumberFormService;
  let phonenumberService: PhonenumberService;
  let userService: UserService;
  let organisationService: OrganisationService;
  let facilityService: FacilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PhonenumberUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PhonenumberUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PhonenumberUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    phonenumberFormService = TestBed.inject(PhonenumberFormService);
    phonenumberService = TestBed.inject(PhonenumberService);
    userService = TestBed.inject(UserService);
    organisationService = TestBed.inject(OrganisationService);
    facilityService = TestBed.inject(FacilityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const phonenumber: IPhonenumber = { id: 456 };
      const user: IUser = { id: 59238 };
      phonenumber.user = user;

      const userCollection: IUser[] = [{ id: 85383 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ phonenumber });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Organisation query and add missing value', () => {
      const phonenumber: IPhonenumber = { id: 456 };
      const organisation: IOrganisation = { id: 85004 };
      phonenumber.organisation = organisation;

      const organisationCollection: IOrganisation[] = [{ id: 44701 }];
      jest.spyOn(organisationService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationCollection })));
      const additionalOrganisations = [organisation];
      const expectedCollection: IOrganisation[] = [...additionalOrganisations, ...organisationCollection];
      jest.spyOn(organisationService, 'addOrganisationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ phonenumber });
      comp.ngOnInit();

      expect(organisationService.query).toHaveBeenCalled();
      expect(organisationService.addOrganisationToCollectionIfMissing).toHaveBeenCalledWith(
        organisationCollection,
        ...additionalOrganisations.map(expect.objectContaining)
      );
      expect(comp.organisationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Facility query and add missing value', () => {
      const phonenumber: IPhonenumber = { id: 456 };
      const facility: IFacility = { id: 11743 };
      phonenumber.facility = facility;

      const facilityCollection: IFacility[] = [{ id: 63462 }];
      jest.spyOn(facilityService, 'query').mockReturnValue(of(new HttpResponse({ body: facilityCollection })));
      const additionalFacilities = [facility];
      const expectedCollection: IFacility[] = [...additionalFacilities, ...facilityCollection];
      jest.spyOn(facilityService, 'addFacilityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ phonenumber });
      comp.ngOnInit();

      expect(facilityService.query).toHaveBeenCalled();
      expect(facilityService.addFacilityToCollectionIfMissing).toHaveBeenCalledWith(
        facilityCollection,
        ...additionalFacilities.map(expect.objectContaining)
      );
      expect(comp.facilitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const phonenumber: IPhonenumber = { id: 456 };
      const user: IUser = { id: 76321 };
      phonenumber.user = user;
      const organisation: IOrganisation = { id: 37283 };
      phonenumber.organisation = organisation;
      const facility: IFacility = { id: 19887 };
      phonenumber.facility = facility;

      activatedRoute.data = of({ phonenumber });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.organisationsSharedCollection).toContain(organisation);
      expect(comp.facilitiesSharedCollection).toContain(facility);
      expect(comp.phonenumber).toEqual(phonenumber);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPhonenumber>>();
      const phonenumber = { id: 123 };
      jest.spyOn(phonenumberFormService, 'getPhonenumber').mockReturnValue(phonenumber);
      jest.spyOn(phonenumberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ phonenumber });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: phonenumber }));
      saveSubject.complete();

      // THEN
      expect(phonenumberFormService.getPhonenumber).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(phonenumberService.update).toHaveBeenCalledWith(expect.objectContaining(phonenumber));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPhonenumber>>();
      const phonenumber = { id: 123 };
      jest.spyOn(phonenumberFormService, 'getPhonenumber').mockReturnValue({ id: null });
      jest.spyOn(phonenumberService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ phonenumber: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: phonenumber }));
      saveSubject.complete();

      // THEN
      expect(phonenumberFormService.getPhonenumber).toHaveBeenCalled();
      expect(phonenumberService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPhonenumber>>();
      const phonenumber = { id: 123 };
      jest.spyOn(phonenumberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ phonenumber });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(phonenumberService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareOrganisation', () => {
      it('Should forward to organisationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(organisationService, 'compareOrganisation');
        comp.compareOrganisation(entity, entity2);
        expect(organisationService.compareOrganisation).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareFacility', () => {
      it('Should forward to facilityService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(facilityService, 'compareFacility');
        comp.compareFacility(entity, entity2);
        expect(facilityService.compareFacility).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
