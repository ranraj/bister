import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EnquiryResponseFormService } from './enquiry-response-form.service';
import { EnquiryResponseService } from '../service/enquiry-response.service';
import { IEnquiryResponse } from '../enquiry-response.model';
import { IAgent } from 'app/entities/agent/agent.model';
import { AgentService } from 'app/entities/agent/service/agent.service';
import { IEnquiry } from 'app/entities/enquiry/enquiry.model';
import { EnquiryService } from 'app/entities/enquiry/service/enquiry.service';

import { EnquiryResponseUpdateComponent } from './enquiry-response-update.component';

describe('EnquiryResponse Management Update Component', () => {
  let comp: EnquiryResponseUpdateComponent;
  let fixture: ComponentFixture<EnquiryResponseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let enquiryResponseFormService: EnquiryResponseFormService;
  let enquiryResponseService: EnquiryResponseService;
  let agentService: AgentService;
  let enquiryService: EnquiryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EnquiryResponseUpdateComponent],
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
      .overrideTemplate(EnquiryResponseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EnquiryResponseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    enquiryResponseFormService = TestBed.inject(EnquiryResponseFormService);
    enquiryResponseService = TestBed.inject(EnquiryResponseService);
    agentService = TestBed.inject(AgentService);
    enquiryService = TestBed.inject(EnquiryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Agent query and add missing value', () => {
      const enquiryResponse: IEnquiryResponse = { id: 456 };
      const agent: IAgent = { id: 23012 };
      enquiryResponse.agent = agent;

      const agentCollection: IAgent[] = [{ id: 58093 }];
      jest.spyOn(agentService, 'query').mockReturnValue(of(new HttpResponse({ body: agentCollection })));
      const additionalAgents = [agent];
      const expectedCollection: IAgent[] = [...additionalAgents, ...agentCollection];
      jest.spyOn(agentService, 'addAgentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ enquiryResponse });
      comp.ngOnInit();

      expect(agentService.query).toHaveBeenCalled();
      expect(agentService.addAgentToCollectionIfMissing).toHaveBeenCalledWith(
        agentCollection,
        ...additionalAgents.map(expect.objectContaining)
      );
      expect(comp.agentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Enquiry query and add missing value', () => {
      const enquiryResponse: IEnquiryResponse = { id: 456 };
      const enquiry: IEnquiry = { id: 68344 };
      enquiryResponse.enquiry = enquiry;

      const enquiryCollection: IEnquiry[] = [{ id: 6349 }];
      jest.spyOn(enquiryService, 'query').mockReturnValue(of(new HttpResponse({ body: enquiryCollection })));
      const additionalEnquiries = [enquiry];
      const expectedCollection: IEnquiry[] = [...additionalEnquiries, ...enquiryCollection];
      jest.spyOn(enquiryService, 'addEnquiryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ enquiryResponse });
      comp.ngOnInit();

      expect(enquiryService.query).toHaveBeenCalled();
      expect(enquiryService.addEnquiryToCollectionIfMissing).toHaveBeenCalledWith(
        enquiryCollection,
        ...additionalEnquiries.map(expect.objectContaining)
      );
      expect(comp.enquiriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const enquiryResponse: IEnquiryResponse = { id: 456 };
      const agent: IAgent = { id: 41495 };
      enquiryResponse.agent = agent;
      const enquiry: IEnquiry = { id: 50052 };
      enquiryResponse.enquiry = enquiry;

      activatedRoute.data = of({ enquiryResponse });
      comp.ngOnInit();

      expect(comp.agentsSharedCollection).toContain(agent);
      expect(comp.enquiriesSharedCollection).toContain(enquiry);
      expect(comp.enquiryResponse).toEqual(enquiryResponse);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEnquiryResponse>>();
      const enquiryResponse = { id: 123 };
      jest.spyOn(enquiryResponseFormService, 'getEnquiryResponse').mockReturnValue(enquiryResponse);
      jest.spyOn(enquiryResponseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ enquiryResponse });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: enquiryResponse }));
      saveSubject.complete();

      // THEN
      expect(enquiryResponseFormService.getEnquiryResponse).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(enquiryResponseService.update).toHaveBeenCalledWith(expect.objectContaining(enquiryResponse));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEnquiryResponse>>();
      const enquiryResponse = { id: 123 };
      jest.spyOn(enquiryResponseFormService, 'getEnquiryResponse').mockReturnValue({ id: null });
      jest.spyOn(enquiryResponseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ enquiryResponse: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: enquiryResponse }));
      saveSubject.complete();

      // THEN
      expect(enquiryResponseFormService.getEnquiryResponse).toHaveBeenCalled();
      expect(enquiryResponseService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEnquiryResponse>>();
      const enquiryResponse = { id: 123 };
      jest.spyOn(enquiryResponseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ enquiryResponse });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(enquiryResponseService.update).toHaveBeenCalled();
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

    describe('compareEnquiry', () => {
      it('Should forward to enquiryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(enquiryService, 'compareEnquiry');
        comp.compareEnquiry(entity, entity2);
        expect(enquiryService.compareEnquiry).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
