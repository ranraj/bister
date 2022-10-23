import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductSpecificationGroupDetailComponent } from './product-specification-group-detail.component';

describe('ProductSpecificationGroup Management Detail Component', () => {
  let comp: ProductSpecificationGroupDetailComponent;
  let fixture: ComponentFixture<ProductSpecificationGroupDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductSpecificationGroupDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productSpecificationGroup: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductSpecificationGroupDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductSpecificationGroupDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productSpecificationGroup on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productSpecificationGroup).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
