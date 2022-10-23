import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProjectSpecificationGroupDetailComponent } from './project-specification-group-detail.component';

describe('ProjectSpecificationGroup Management Detail Component', () => {
  let comp: ProjectSpecificationGroupDetailComponent;
  let fixture: ComponentFixture<ProjectSpecificationGroupDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectSpecificationGroupDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ projectSpecificationGroup: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProjectSpecificationGroupDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProjectSpecificationGroupDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load projectSpecificationGroup on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.projectSpecificationGroup).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
