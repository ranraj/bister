import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AgentDetailComponent } from './agent-detail.component';

describe('Agent Management Detail Component', () => {
  let comp: AgentDetailComponent;
  let fixture: ComponentFixture<AgentDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AgentDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ agent: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AgentDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AgentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load agent on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.agent).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
