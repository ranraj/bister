import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProjectReviewDetailComponent } from './project-review-detail.component';

describe('ProjectReview Management Detail Component', () => {
  let comp: ProjectReviewDetailComponent;
  let fixture: ComponentFixture<ProjectReviewDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectReviewDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ projectReview: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProjectReviewDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProjectReviewDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load projectReview on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.projectReview).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
