import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EnquiryFormService } from './enquiry-form.service';
import { EnquiryService } from '../service/enquiry.service';
import { IEnquiry } from '../enquiry.model';
import { IAgent } from 'app/entities/agent/agent.model';
import { AgentService } from 'app/entities/agent/service/agent.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';

import { EnquiryUpdateComponent } from './enquiry-update.component';

describe('Enquiry Management Update Component', () => {
  let comp: EnquiryUpdateComponent;
  let fixture: ComponentFixture<EnquiryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let enquiryFormService: EnquiryFormService;
  let enquiryService: EnquiryService;
  let agentService: AgentService;
  let projectService: ProjectService;
  let productService: ProductService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EnquiryUpdateComponent],
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
      .overrideTemplate(EnquiryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EnquiryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    enquiryFormService = TestBed.inject(EnquiryFormService);
    enquiryService = TestBed.inject(EnquiryService);
    agentService = TestBed.inject(AgentService);
    projectService = TestBed.inject(ProjectService);
    productService = TestBed.inject(ProductService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Agent query and add missing value', () => {
      const enquiry: IEnquiry = { id: 456 };
      const agent: IAgent = { id: 92234 };
      enquiry.agent = agent;

      const agentCollection: IAgent[] = [{ id: 99157 }];
      jest.spyOn(agentService, 'query').mockReturnValue(of(new HttpResponse({ body: agentCollection })));
      const additionalAgents = [agent];
      const expectedCollection: IAgent[] = [...additionalAgents, ...agentCollection];
      jest.spyOn(agentService, 'addAgentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ enquiry });
      comp.ngOnInit();

      expect(agentService.query).toHaveBeenCalled();
      expect(agentService.addAgentToCollectionIfMissing).toHaveBeenCalledWith(
        agentCollection,
        ...additionalAgents.map(expect.objectContaining)
      );
      expect(comp.agentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Project query and add missing value', () => {
      const enquiry: IEnquiry = { id: 456 };
      const project: IProject = { id: 39256 };
      enquiry.project = project;

      const projectCollection: IProject[] = [{ id: 77427 }];
      jest.spyOn(projectService, 'query').mockReturnValue(of(new HttpResponse({ body: projectCollection })));
      const additionalProjects = [project];
      const expectedCollection: IProject[] = [...additionalProjects, ...projectCollection];
      jest.spyOn(projectService, 'addProjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ enquiry });
      comp.ngOnInit();

      expect(projectService.query).toHaveBeenCalled();
      expect(projectService.addProjectToCollectionIfMissing).toHaveBeenCalledWith(
        projectCollection,
        ...additionalProjects.map(expect.objectContaining)
      );
      expect(comp.projectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Product query and add missing value', () => {
      const enquiry: IEnquiry = { id: 456 };
      const product: IProduct = { id: 15661 };
      enquiry.product = product;

      const productCollection: IProduct[] = [{ id: 43604 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ enquiry });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Customer query and add missing value', () => {
      const enquiry: IEnquiry = { id: 456 };
      const customer: ICustomer = { id: 80760 };
      enquiry.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 53544 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ enquiry });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const enquiry: IEnquiry = { id: 456 };
      const agent: IAgent = { id: 83476 };
      enquiry.agent = agent;
      const project: IProject = { id: 27507 };
      enquiry.project = project;
      const product: IProduct = { id: 41880 };
      enquiry.product = product;
      const customer: ICustomer = { id: 6942 };
      enquiry.customer = customer;

      activatedRoute.data = of({ enquiry });
      comp.ngOnInit();

      expect(comp.agentsSharedCollection).toContain(agent);
      expect(comp.projectsSharedCollection).toContain(project);
      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.customersSharedCollection).toContain(customer);
      expect(comp.enquiry).toEqual(enquiry);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEnquiry>>();
      const enquiry = { id: 123 };
      jest.spyOn(enquiryFormService, 'getEnquiry').mockReturnValue(enquiry);
      jest.spyOn(enquiryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ enquiry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: enquiry }));
      saveSubject.complete();

      // THEN
      expect(enquiryFormService.getEnquiry).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(enquiryService.update).toHaveBeenCalledWith(expect.objectContaining(enquiry));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEnquiry>>();
      const enquiry = { id: 123 };
      jest.spyOn(enquiryFormService, 'getEnquiry').mockReturnValue({ id: null });
      jest.spyOn(enquiryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ enquiry: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: enquiry }));
      saveSubject.complete();

      // THEN
      expect(enquiryFormService.getEnquiry).toHaveBeenCalled();
      expect(enquiryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEnquiry>>();
      const enquiry = { id: 123 };
      jest.spyOn(enquiryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ enquiry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(enquiryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareAgent', () => {
      it('Should forward to agentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(agentService, 'compareAgent');
        comp.compareAgent(entity, entity2);
        expect(agentService.compareAgent).toHaveBeenCalledWith(entity, entity2);
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

    describe('compareProduct', () => {
      it('Should forward to productService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productService, 'compareProduct');
        comp.compareProduct(entity, entity2);
        expect(productService.compareProduct).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
