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

describe('TaxRate e2e test', () => {
  const taxRatePageUrl = '/tax-rate';
  const taxRatePageUrlPattern = new RegExp('/tax-rate(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const taxRateSample = {
    country: 'Guernsey',
    state: 'Cotton programming',
    postcode: 'Designer',
    city: 'Chelseyborough',
    rate: 'Baby Table maroon',
    name: 'Circle Avon',
    compound: true,
    priority: 78782,
  };

  let taxRate;
  let taxClass;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/tax-classes',
      body: { name: 'Market Chair', slug: 'Handmade SDD Personal' },
    }).then(({ body }) => {
      taxClass = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tax-rates+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tax-rates').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tax-rates/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/tax-classes', {
      statusCode: 200,
      body: [taxClass],
    });
  });

  afterEach(() => {
    if (taxRate) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tax-rates/${taxRate.id}`,
      }).then(() => {
        taxRate = undefined;
      });
    }
  });

  afterEach(() => {
    if (taxClass) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tax-classes/${taxClass.id}`,
      }).then(() => {
        taxClass = undefined;
      });
    }
  });

  it('TaxRates menu should load TaxRates page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('tax-rate');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TaxRate').should('exist');
    cy.url().should('match', taxRatePageUrlPattern);
  });

  describe('TaxRate page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(taxRatePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TaxRate page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/tax-rate/new$'));
        cy.getEntityCreateUpdateHeading('TaxRate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxRatePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tax-rates',
          body: {
            ...taxRateSample,
            taxClass: taxClass,
          },
        }).then(({ body }) => {
          taxRate = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tax-rates+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/tax-rates?page=0&size=20>; rel="last",<http://localhost/api/tax-rates?page=0&size=20>; rel="first"',
              },
              body: [taxRate],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(taxRatePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TaxRate page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('taxRate');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxRatePageUrlPattern);
      });

      it('edit button click should load edit TaxRate page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TaxRate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxRatePageUrlPattern);
      });

      it('edit button click should load edit TaxRate page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TaxRate');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxRatePageUrlPattern);
      });

      it('last delete button click should delete instance of TaxRate', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('taxRate').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxRatePageUrlPattern);

        taxRate = undefined;
      });
    });
  });

  describe('new TaxRate page', () => {
    beforeEach(() => {
      cy.visit(`${taxRatePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TaxRate');
    });

    it('should create an instance of TaxRate', () => {
      cy.get(`[data-cy="country"]`).type('Colombia').should('have.value', 'Colombia');

      cy.get(`[data-cy="state"]`).type('generating Movies').should('have.value', 'generating Movies');

      cy.get(`[data-cy="postcode"]`).type('asynchronous Cambrid').should('have.value', 'asynchronous Cambrid');

      cy.get(`[data-cy="city"]`).type('Willmsbury').should('have.value', 'Willmsbury');

      cy.get(`[data-cy="rate"]`).type('Granite').should('have.value', 'Granite');

      cy.get(`[data-cy="name"]`).type('Streamlined').should('have.value', 'Streamlined');

      cy.get(`[data-cy="compound"]`).should('not.be.checked');
      cy.get(`[data-cy="compound"]`).click().should('be.checked');

      cy.get(`[data-cy="priority"]`).type('49755').should('have.value', '49755');

      cy.get(`[data-cy="taxClass"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        taxRate = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', taxRatePageUrlPattern);
    });
  });
});
