import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IProductSpecificationGroup } from '../product-specification-group.model';
import { ProductSpecificationGroupService } from '../service/product-specification-group.service';

import { ProductSpecificationGroupRoutingResolveService } from './product-specification-group-routing-resolve.service';

describe('ProductSpecificationGroup routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ProductSpecificationGroupRoutingResolveService;
  let service: ProductSpecificationGroupService;
  let resultProductSpecificationGroup: IProductSpecificationGroup | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(ProductSpecificationGroupRoutingResolveService);
    service = TestBed.inject(ProductSpecificationGroupService);
    resultProductSpecificationGroup = undefined;
  });

  describe('resolve', () => {
    it('should return IProductSpecificationGroup returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProductSpecificationGroup = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProductSpecificationGroup).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProductSpecificationGroup = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultProductSpecificationGroup).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IProductSpecificationGroup>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProductSpecificationGroup = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProductSpecificationGroup).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
