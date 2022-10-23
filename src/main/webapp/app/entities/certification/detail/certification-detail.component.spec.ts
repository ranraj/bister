import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CertificationDetailComponent } from './certification-detail.component';

describe('Certification Management Detail Component', () => {
  let comp: CertificationDetailComponent;
  let fixture: ComponentFixture<CertificationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CertificationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ certification: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CertificationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CertificationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load certification on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.certification).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
