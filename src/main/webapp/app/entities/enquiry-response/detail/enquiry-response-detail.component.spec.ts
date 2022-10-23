import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EnquiryResponseDetailComponent } from './enquiry-response-detail.component';

describe('EnquiryResponse Management Detail Component', () => {
  let comp: EnquiryResponseDetailComponent;
  let fixture: ComponentFixture<EnquiryResponseDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EnquiryResponseDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ enquiryResponse: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EnquiryResponseDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EnquiryResponseDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load enquiryResponse on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.enquiryResponse).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
