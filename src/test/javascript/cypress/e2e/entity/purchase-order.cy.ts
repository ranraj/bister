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

describe('PurchaseOrder e2e test', () => {
  const purchaseOrderPageUrl = '/purchase-order';
  const purchaseOrderPageUrlPattern = new RegExp('/purchase-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const purchaseOrderSample = {"placedDate":"2022-10-22T15:06:13.194Z","status":"HOLD","deliveryOption":"RENT"};

  let purchaseOrder;
  // let transaction;
  // let user;
  // let productVariation;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/transactions',
      body: {"code":"Engineer","amount":89128,"status":"COMPLETE"},
    }).then(({ body }) => {
      transaction = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"deposit USB","firstName":"Maud","lastName":"Ankunding"},
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/product-variations',
      body: {"assetId":"Fuerte Cambridgeshire","name":"Colombian","description":"Car Future","regularPrice":58643,"salePrice":83195,"dateOnSaleFrom":"2022-10-22","dateOnSaleTo":"2022-10-22","isDraft":true,"useParentDetails":true,"saleStatus":"DELIVERY"},
    }).then(({ body }) => {
      productVariation = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/purchase-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/purchase-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/purchase-orders/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/payment-schedules', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/transactions', {
      statusCode: 200,
      body: [transaction],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/product-variations', {
      statusCode: 200,
      body: [productVariation],
    });

    cy.intercept('GET', '/api/invoices', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (purchaseOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/purchase-orders/${purchaseOrder.id}`,
      }).then(() => {
        purchaseOrder = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (transaction) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/transactions/${transaction.id}`,
      }).then(() => {
        transaction = undefined;
      });
    }
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
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

  it('PurchaseOrders menu should load PurchaseOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('purchase-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PurchaseOrder').should('exist');
    cy.url().should('match', purchaseOrderPageUrlPattern);
  });

  describe('PurchaseOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(purchaseOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PurchaseOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/purchase-order/new$'));
        cy.getEntityCreateUpdateHeading('PurchaseOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/purchase-orders',
          body: {
            ...purchaseOrderSample,
            transaction: transaction,
            user: user,
            productVariation: productVariation,
          },
        }).then(({ body }) => {
          purchaseOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/purchase-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/purchase-orders?page=0&size=20>; rel="last",<http://localhost/api/purchase-orders?page=0&size=20>; rel="first"',
              },
              body: [purchaseOrder],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(purchaseOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(purchaseOrderPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details PurchaseOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('purchaseOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);
      });

      it('edit button click should load edit PurchaseOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PurchaseOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);
      });

      it('edit button click should load edit PurchaseOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PurchaseOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of PurchaseOrder', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('purchaseOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);

        purchaseOrder = undefined;
      });
    });
  });

  describe('new PurchaseOrder page', () => {
    beforeEach(() => {
      cy.visit(`${purchaseOrderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PurchaseOrder');
    });

    it.skip('should create an instance of PurchaseOrder', () => {
      cy.get(`[data-cy="placedDate"]`).type('2022-10-22T18:43').blur().should('have.value', '2022-10-22T18:43');

      cy.get(`[data-cy="status"]`).select('PAYMENT_PENDING');

      cy.get(`[data-cy="code"]`).type('SavingsXXXXXXXXXXXXX').should('have.value', 'SavingsXXXXXXXXXXXXX');

      cy.get(`[data-cy="deliveryOption"]`).select('CONSTRUCTION_HANDOVER');

      cy.get(`[data-cy="transaction"]`).select([0]);
      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="productVariation"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        purchaseOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', purchaseOrderPageUrlPattern);
    });
  });
});
