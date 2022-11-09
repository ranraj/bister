import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AttachmentFormService } from './attachment-form.service';
import { AttachmentService } from '../service/attachment.service';
import { IAttachment } from '../attachment.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { IEnquiry } from 'app/enquiry/enquiry.model';
import { EnquiryService } from 'app/enquiry/service/enquiry.service';
import { ICertification } from 'app/entities/certification/certification.model';
import { CertificationService } from 'app/entities/certification/service/certification.service';
import { IProductSpecification } from 'app/entities/product-specification/product-specification.model';
import { ProductSpecificationService } from 'app/entities/product-specification/service/product-specification.service';
import { IProjectSpecification } from 'app/entities/project-specification/project-specification.model';
import { ProjectSpecificationService } from 'app/entities/project-specification/service/project-specification.service';

import { AttachmentUpdateComponent } from './attachment-update.component';

describe('Attachment Management Update Component', () => {
  let comp: AttachmentUpdateComponent;
  let fixture: ComponentFixture<AttachmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let attachmentFormService: AttachmentFormService;
  let attachmentService: AttachmentService;
  let productService: ProductService;
  let projectService: ProjectService;
  let enquiryService: EnquiryService;
  let certificationService: CertificationService;
  let productSpecificationService: ProductSpecificationService;
  let projectSpecificationService: ProjectSpecificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AttachmentUpdateComponent],
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
      .overrideTemplate(AttachmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AttachmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    attachmentFormService = TestBed.inject(AttachmentFormService);
    attachmentService = TestBed.inject(AttachmentService);
    productService = TestBed.inject(ProductService);
    projectService = TestBed.inject(ProjectService);
    enquiryService = TestBed.inject(EnquiryService);
    certificationService = TestBed.inject(CertificationService);
    productSpecificationService = TestBed.inject(ProductSpecificationService);
    projectSpecificationService = TestBed.inject(ProjectSpecificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Product query and add missing value', () => {
      const attachment: IAttachment = { id: 456 };
      const product: IProduct = { id: 3816 };
      attachment.product = product;

      const productCollection: IProduct[] = [{ id: 31978 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Project query and add missing value', () => {
      const attachment: IAttachment = { id: 456 };
      const project: IProject = { id: 36311 };
      attachment.project = project;

      const projectCollection: IProject[] = [{ id: 13443 }];
      jest.spyOn(projectService, 'query').mockReturnValue(of(new HttpResponse({ body: projectCollection })));
      const additionalProjects = [project];
      const expectedCollection: IProject[] = [...additionalProjects, ...projectCollection];
      jest.spyOn(projectService, 'addProjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(projectService.query).toHaveBeenCalled();
      expect(projectService.addProjectToCollectionIfMissing).toHaveBeenCalledWith(
        projectCollection,
        ...additionalProjects.map(expect.objectContaining)
      );
      expect(comp.projectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Enquiry query and add missing value', () => {
      const attachment: IAttachment = { id: 456 };
      const enquiry: IEnquiry = { id: 64842 };
      attachment.enquiry = enquiry;

      const enquiryCollection: IEnquiry[] = [{ id: 60346 }];
      jest.spyOn(enquiryService, 'query').mockReturnValue(of(new HttpResponse({ body: enquiryCollection })));
      const additionalEnquiries = [enquiry];
      const expectedCollection: IEnquiry[] = [...additionalEnquiries, ...enquiryCollection];
      jest.spyOn(enquiryService, 'addEnquiryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(enquiryService.query).toHaveBeenCalled();
      expect(enquiryService.addEnquiryToCollectionIfMissing).toHaveBeenCalledWith(
        enquiryCollection,
        ...additionalEnquiries.map(expect.objectContaining)
      );
      expect(comp.enquiriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Certification query and add missing value', () => {
      const attachment: IAttachment = { id: 456 };
      const certification: ICertification = { id: 66507 };
      attachment.certification = certification;

      const certificationCollection: ICertification[] = [{ id: 65316 }];
      jest.spyOn(certificationService, 'query').mockReturnValue(of(new HttpResponse({ body: certificationCollection })));
      const additionalCertifications = [certification];
      const expectedCollection: ICertification[] = [...additionalCertifications, ...certificationCollection];
      jest.spyOn(certificationService, 'addCertificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(certificationService.query).toHaveBeenCalled();
      expect(certificationService.addCertificationToCollectionIfMissing).toHaveBeenCalledWith(
        certificationCollection,
        ...additionalCertifications.map(expect.objectContaining)
      );
      expect(comp.certificationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ProductSpecification query and add missing value', () => {
      const attachment: IAttachment = { id: 456 };
      const productSpecification: IProductSpecification = { id: 11585 };
      attachment.productSpecification = productSpecification;

      const productSpecificationCollection: IProductSpecification[] = [{ id: 75526 }];
      jest.spyOn(productSpecificationService, 'query').mockReturnValue(of(new HttpResponse({ body: productSpecificationCollection })));
      const additionalProductSpecifications = [productSpecification];
      const expectedCollection: IProductSpecification[] = [...additionalProductSpecifications, ...productSpecificationCollection];
      jest.spyOn(productSpecificationService, 'addProductSpecificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(productSpecificationService.query).toHaveBeenCalled();
      expect(productSpecificationService.addProductSpecificationToCollectionIfMissing).toHaveBeenCalledWith(
        productSpecificationCollection,
        ...additionalProductSpecifications.map(expect.objectContaining)
      );
      expect(comp.productSpecificationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ProjectSpecification query and add missing value', () => {
      const attachment: IAttachment = { id: 456 };
      const projectSpecification: IProjectSpecification = { id: 8099 };
      attachment.projectSpecification = projectSpecification;

      const projectSpecificationCollection: IProjectSpecification[] = [{ id: 33099 }];
      jest.spyOn(projectSpecificationService, 'query').mockReturnValue(of(new HttpResponse({ body: projectSpecificationCollection })));
      const additionalProjectSpecifications = [projectSpecification];
      const expectedCollection: IProjectSpecification[] = [...additionalProjectSpecifications, ...projectSpecificationCollection];
      jest.spyOn(projectSpecificationService, 'addProjectSpecificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(projectSpecificationService.query).toHaveBeenCalled();
      expect(projectSpecificationService.addProjectSpecificationToCollectionIfMissing).toHaveBeenCalledWith(
        projectSpecificationCollection,
        ...additionalProjectSpecifications.map(expect.objectContaining)
      );
      expect(comp.projectSpecificationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const attachment: IAttachment = { id: 456 };
      const product: IProduct = { id: 10910 };
      attachment.product = product;
      const project: IProject = { id: 1682 };
      attachment.project = project;
      const enquiry: IEnquiry = { id: 5199 };
      attachment.enquiry = enquiry;
      const certification: ICertification = { id: 80406 };
      attachment.certification = certification;
      const productSpecification: IProductSpecification = { id: 82358 };
      attachment.productSpecification = productSpecification;
      const projectSpecification: IProjectSpecification = { id: 64195 };
      attachment.projectSpecification = projectSpecification;

      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.projectsSharedCollection).toContain(project);
      expect(comp.enquiriesSharedCollection).toContain(enquiry);
      expect(comp.certificationsSharedCollection).toContain(certification);
      expect(comp.productSpecificationsSharedCollection).toContain(productSpecification);
      expect(comp.projectSpecificationsSharedCollection).toContain(projectSpecification);
      expect(comp.attachment).toEqual(attachment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAttachment>>();
      const attachment = { id: 123 };
      jest.spyOn(attachmentFormService, 'getAttachment').mockReturnValue(attachment);
      jest.spyOn(attachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: attachment }));
      saveSubject.complete();

      // THEN
      expect(attachmentFormService.getAttachment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(attachmentService.update).toHaveBeenCalledWith(expect.objectContaining(attachment));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAttachment>>();
      const attachment = { id: 123 };
      jest.spyOn(attachmentFormService, 'getAttachment').mockReturnValue({ id: null });
      jest.spyOn(attachmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ attachment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: attachment }));
      saveSubject.complete();

      // THEN
      expect(attachmentFormService.getAttachment).toHaveBeenCalled();
      expect(attachmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAttachment>>();
      const attachment = { id: 123 };
      jest.spyOn(attachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ attachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(attachmentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProduct', () => {
      it('Should forward to productService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productService, 'compareProduct');
        comp.compareProduct(entity, entity2);
        expect(productService.compareProduct).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProject', () => {
      it('Should forward to projectService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(projectService, 'compareProject');
        comp.compareProject(entity, entity2);
        expect(projectService.compareProject).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEnquiry', () => {
      it('Should forward to enquiryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(enquiryService, 'compareEnquiry');
        comp.compareEnquiry(entity, entity2);
        expect(enquiryService.compareEnquiry).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCertification', () => {
      it('Should forward to certificationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(certificationService, 'compareCertification');
        comp.compareCertification(entity, entity2);
        expect(certificationService.compareCertification).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProductSpecification', () => {
      it('Should forward to productSpecificationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productSpecificationService, 'compareProductSpecification');
        comp.compareProductSpecification(entity, entity2);
        expect(productSpecificationService.compareProductSpecification).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProjectSpecification', () => {
      it('Should forward to projectSpecificationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(projectSpecificationService, 'compareProjectSpecification');
        comp.compareProjectSpecification(entity, entity2);
        expect(projectSpecificationService.compareProjectSpecification).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
