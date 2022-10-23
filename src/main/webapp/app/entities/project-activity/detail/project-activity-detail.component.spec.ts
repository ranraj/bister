import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProjectActivityDetailComponent } from './project-activity-detail.component';

describe('ProjectActivity Management Detail Component', () => {
  let comp: ProjectActivityDetailComponent;
  let fixture: ComponentFixture<ProjectActivityDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectActivityDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ projectActivity: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProjectActivityDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProjectActivityDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load projectActivity on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.projectActivity).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
