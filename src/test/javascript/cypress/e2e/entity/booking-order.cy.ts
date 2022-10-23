import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('BookingOrder e2e test', () => {
  const bookingOrderPageUrl = '/booking-order';
  const bookingOrderPageUrlPattern = new RegExp('/booking-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const bookingOrderSample = {"placedDate":"2022-10-22T08:39:57.821Z","status":"BOOKED"};

  let bookingOrder;
  // let productVariation;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/product-variations',
      body: {"assetId":"Knolls","name":"payment","description":"Cape Glens","regularPrice":96403,"salePrice":50692,"dateOnSaleFrom":"2022-10-22","dateOnSaleTo":"2022-10-22","isDraft":true,"useParentDetails":false,"saleStatus":"OPEN"},
    }).then(({ body }) => {
      productVariation = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/booking-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/booking-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/booking-orders/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/product-variations', {
      statusCode: 200,
      body: [productVariation],
    });

  });
   */

  afterEach(() => {
    if (bookingOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/booking-orders/${bookingOrder.id}`,
      }).then(() => {
        bookingOrder = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (productVariation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-variations/${productVariation.id}`,
      }).then(() => {
        productVariation = undefined;
      });
    }
  });
   */

  it('BookingOrders menu should load BookingOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('booking-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BookingOrder').should('exist');
    cy.url().should('match', bookingOrderPageUrlPattern);
  });

  describe('BookingOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(bookingOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BookingOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/booking-order/new$'));
        cy.getEntityCreateUpdateHeading('BookingOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/booking-orders',
          body: {
            ...bookingOrderSample,
            productVariation: productVariation,
          },
        }).then(({ body }) => {
          bookingOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/booking-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/booking-orders?page=0&size=20>; rel="last",<http://localhost/api/booking-orders?page=0&size=20>; rel="first"',
              },
              body: [bookingOrder],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(bookingOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(bookingOrderPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details BookingOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('bookingOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingOrderPageUrlPattern);
      });

      it('edit button click should load edit BookingOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BookingOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingOrderPageUrlPattern);
      });

      it('edit button click should load edit BookingOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BookingOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingOrderPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of BookingOrder', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('bookingOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingOrderPageUrlPattern);

        bookingOrder = undefined;
      });
    });
  });

  describe('new BookingOrder page', () => {
    beforeEach(() => {
      cy.visit(`${bookingOrderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BookingOrder');
    });

    it.skip('should create an instance of BookingOrder', () => {
      cy.get(`[data-cy="placedDate"]`).type('2022-10-22T23:21').blur().should('have.value', '2022-10-22T23:21');

      cy.get(`[data-cy="status"]`).select('BLOCKED');

      cy.get(`[data-cy="code"]`).type('AssuranceXXXXXXXXXXX').should('have.value', 'AssuranceXXXXXXXXXXX');

      cy.get(`[data-cy="bookingExpieryDate"]`).type('2022-10-22T10:05').blur().should('have.value', '2022-10-22T10:05');

      cy.get(`[data-cy="productVariation"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        bookingOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', bookingOrderPageUrlPattern);
    });
  });
});
