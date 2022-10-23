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

describe('Refund e2e test', () => {
  const refundPageUrl = '/refund';
  const refundPageUrlPattern = new RegExp('/refund(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const refundSample = {"amount":36327,"reason":"invoice","orderCode":63681,"status":"COMPLETE"};

  let refund;
  // let transaction;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/transactions',
      body: {"code":"Shoes","amount":85159,"status":"PENDING"},
    }).then(({ body }) => {
      transaction = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"Operations SQL","firstName":"Guy","lastName":"Ryan"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/refunds+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/refunds').as('postEntityRequest');
    cy.intercept('DELETE', '/api/refunds/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/transactions', {
      statusCode: 200,
      body: [transaction],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (refund) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/refunds/${refund.id}`,
      }).then(() => {
        refund = undefined;
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
  });
   */

  it('Refunds menu should load Refunds page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('refund');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Refund').should('exist');
    cy.url().should('match', refundPageUrlPattern);
  });

  describe('Refund page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(refundPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Refund page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/refund/new$'));
        cy.getEntityCreateUpdateHeading('Refund');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', refundPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/refunds',
          body: {
            ...refundSample,
            transaction: transaction,
            user: user,
          },
        }).then(({ body }) => {
          refund = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/refunds+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/refunds?page=0&size=20>; rel="last",<http://localhost/api/refunds?page=0&size=20>; rel="first"',
              },
              body: [refund],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(refundPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(refundPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Refund page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('refund');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', refundPageUrlPattern);
      });

      it('edit button click should load edit Refund page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Refund');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', refundPageUrlPattern);
      });

      it('edit button click should load edit Refund page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Refund');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', refundPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Refund', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('refund').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', refundPageUrlPattern);

        refund = undefined;
      });
    });
  });

  describe('new Refund page', () => {
    beforeEach(() => {
      cy.visit(`${refundPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Refund');
    });

    it.skip('should create an instance of Refund', () => {
      cy.get(`[data-cy="amount"]`).type('40947').should('have.value', '40947');

      cy.get(`[data-cy="reason"]`).type('schemas').should('have.value', 'schemas');

      cy.get(`[data-cy="orderCode"]`).type('23703').should('have.value', '23703');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="transaction"]`).select(1);
      cy.get(`[data-cy="user"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        refund = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', refundPageUrlPattern);
    });
  });
});
