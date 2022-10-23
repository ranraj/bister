import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RefundDetailComponent } from './refund-detail.component';

describe('Refund Management Detail Component', () => {
  let comp: RefundDetailComponent;
  let fixture: ComponentFixture<RefundDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefundDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ refund: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RefundDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RefundDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load refund on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.refund).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
