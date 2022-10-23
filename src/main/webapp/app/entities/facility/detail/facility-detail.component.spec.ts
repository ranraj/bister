import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FacilityDetailComponent } from './facility-detail.component';

describe('Facility Management Detail Component', () => {
  let comp: FacilityDetailComponent;
  let fixture: ComponentFixture<FacilityDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FacilityDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ facility: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FacilityDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FacilityDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load facility on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.facility).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
