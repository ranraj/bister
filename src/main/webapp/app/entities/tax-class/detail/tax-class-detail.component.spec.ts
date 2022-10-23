import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TaxClassDetailComponent } from './tax-class-detail.component';

describe('TaxClass Management Detail Component', () => {
  let comp: TaxClassDetailComponent;
  let fixture: ComponentFixture<TaxClassDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TaxClassDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ taxClass: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TaxClassDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TaxClassDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load taxClass on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.taxClass).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
