import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProjectTypeDetailComponent } from './project-type-detail.component';

describe('ProjectType Management Detail Component', () => {
  let comp: ProjectTypeDetailComponent;
  let fixture: ComponentFixture<ProjectTypeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectTypeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ projectType: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProjectTypeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProjectTypeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load projectType on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.projectType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
