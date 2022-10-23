import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrganisationFormService } from './organisation-form.service';
import { OrganisationService } from '../service/organisation.service';
import { IOrganisation } from '../organisation.model';
import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';
import { IBusinessPartner } from 'app/entities/business-partner/business-partner.model';
import { BusinessPartnerService } from 'app/entities/business-partner/service/business-partner.service';

import { OrganisationUpdateComponent } from './organisation-update.component';

describe('Organisation Management Update Component', () => {
  let comp: OrganisationUpdateComponent;
  let fixture: ComponentFixture<OrganisationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let organisationFormService: OrganisationFormService;
  let organisationService: OrganisationService;
  let addressService: AddressService;
  let businessPartnerService: BusinessPartnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrganisationUpdateComponent],
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
      .overrideTemplate(OrganisationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrganisationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    organisationFormService = TestBed.inject(OrganisationFormService);
    organisationService = TestBed.inject(OrganisationService);
    addressService = TestBed.inject(AddressService);
    businessPartnerService = TestBed.inject(BusinessPartnerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Address query and add missing value', () => {
      const organisation: IOrganisation = { id: 456 };
      const address: IAddress = { id: 57024 };
      organisation.address = address;

      const addressCollection: IAddress[] = [{ id: 77772 }];
      jest.spyOn(addressService, 'query').mockReturnValue(of(new HttpResponse({ body: addressCollection })));
      const additionalAddresses = [address];
      const expectedCollection: IAddress[] = [...additionalAddresses, ...addressCollection];
      jest.spyOn(addressService, 'addAddressToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisation });
      comp.ngOnInit();

      expect(addressService.query).toHaveBeenCalled();
      expect(addressService.addAddressToCollectionIfMissing).toHaveBeenCalledWith(
        addressCollection,
        ...additionalAddresses.map(expect.objectContaining)
      );
      expect(comp.addressesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call BusinessPartner query and add missing value', () => {
      const organisation: IOrganisation = { id: 456 };
      const businessPartner: IBusinessPartner = { id: 9008 };
      organisation.businessPartner = businessPartner;

      const businessPartnerCollection: IBusinessPartner[] = [{ id: 97353 }];
      jest.spyOn(businessPartnerService, 'query').mockReturnValue(of(new HttpResponse({ body: businessPartnerCollection })));
      const additionalBusinessPartners = [businessPartner];
      const expectedCollection: IBusinessPartner[] = [...additionalBusinessPartners, ...businessPartnerCollection];
      jest.spyOn(businessPartnerService, 'addBusinessPartnerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisation });
      comp.ngOnInit();

      expect(businessPartnerService.query).toHaveBeenCalled();
      expect(businessPartnerService.addBusinessPartnerToCollectionIfMissing).toHaveBeenCalledWith(
        businessPartnerCollection,
        ...additionalBusinessPartners.map(expect.objectContaining)
      );
      expect(comp.businessPartnersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const organisation: IOrganisation = { id: 456 };
      const address: IAddress = { id: 68917 };
      organisation.address = address;
      const businessPartner: IBusinessPartner = { id: 6506 };
      organisation.businessPartner = businessPartner;

      activatedRoute.data = of({ organisation });
      comp.ngOnInit();

      expect(comp.addressesSharedCollection).toContain(address);
      expect(comp.businessPartnersSharedCollection).toContain(businessPartner);
      expect(comp.organisation).toEqual(organisation);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisation>>();
      const organisation = { id: 123 };
      jest.spyOn(organisationFormService, 'getOrganisation').mockReturnValue(organisation);
      jest.spyOn(organisationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisation }));
      saveSubject.complete();

      // THEN
      expect(organisationFormService.getOrganisation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(organisationService.update).toHaveBeenCalledWith(expect.objectContaining(organisation));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisation>>();
      const organisation = { id: 123 };
      jest.spyOn(organisationFormService, 'getOrganisation').mockReturnValue({ id: null });
      jest.spyOn(organisationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisation }));
      saveSubject.complete();

      // THEN
      expect(organisationFormService.getOrganisation).toHaveBeenCalled();
      expect(organisationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisation>>();
      const organisation = { id: 123 };
      jest.spyOn(organisationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(organisationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareAddress', () => {
      it('Should forward to addressService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(addressService, 'compareAddress');
        comp.compareAddress(entity, entity2);
        expect(addressService.compareAddress).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBusinessPartner', () => {
      it('Should forward to businessPartnerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(businessPartnerService, 'compareBusinessPartner');
        comp.compareBusinessPartner(entity, entity2);
        expect(businessPartnerService.compareBusinessPartner).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
