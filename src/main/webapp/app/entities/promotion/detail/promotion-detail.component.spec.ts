import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PromotionDetailComponent } from './promotion-detail.component';

describe('Promotion Management Detail Component', () => {
  let comp: PromotionDetailComponent;
  let fixture: ComponentFixture<PromotionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PromotionDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ promotion: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PromotionDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PromotionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load promotion on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.promotion).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
