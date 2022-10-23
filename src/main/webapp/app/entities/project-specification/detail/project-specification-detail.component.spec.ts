import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProjectSpecificationDetailComponent } from './project-specification-detail.component';

describe('ProjectSpecification Management Detail Component', () => {
  let comp: ProjectSpecificationDetailComponent;
  let fixture: ComponentFixture<ProjectSpecificationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectSpecificationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ projectSpecification: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProjectSpecificationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProjectSpecificationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load projectSpecification on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.projectSpecification).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
