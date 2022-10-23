import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BusinessPartnerDetailComponent } from './business-partner-detail.component';

describe('BusinessPartner Management Detail Component', () => {
  let comp: BusinessPartnerDetailComponent;
  let fixture: ComponentFixture<BusinessPartnerDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BusinessPartnerDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ businessPartner: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BusinessPartnerDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BusinessPartnerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load businessPartner on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.businessPartner).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
