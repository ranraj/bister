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

describe('TaxClass e2e test', () => {
  const taxClassPageUrl = '/tax-class';
  const taxClassPageUrlPattern = new RegExp('/tax-class(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const taxClassSample = { name: 'USB', slug: 'AI strategize Checking' };

  let taxClass;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tax-classes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tax-classes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tax-classes/*').as('deleteEntityRequest');
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

  it('TaxClasses menu should load TaxClasses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('tax-class');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TaxClass').should('exist');
    cy.url().should('match', taxClassPageUrlPattern);
  });

  describe('TaxClass page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(taxClassPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TaxClass page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/tax-class/new$'));
        cy.getEntityCreateUpdateHeading('TaxClass');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxClassPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tax-classes',
          body: taxClassSample,
        }).then(({ body }) => {
          taxClass = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tax-classes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/tax-classes?page=0&size=20>; rel="last",<http://localhost/api/tax-classes?page=0&size=20>; rel="first"',
              },
              body: [taxClass],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(taxClassPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TaxClass page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('taxClass');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxClassPageUrlPattern);
      });

      it('edit button click should load edit TaxClass page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TaxClass');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxClassPageUrlPattern);
      });

      it('edit button click should load edit TaxClass page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TaxClass');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxClassPageUrlPattern);
      });

      it('last delete button click should delete instance of TaxClass', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('taxClass').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', taxClassPageUrlPattern);

        taxClass = undefined;
      });
    });
  });

  describe('new TaxClass page', () => {
    beforeEach(() => {
      cy.visit(`${taxClassPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TaxClass');
    });

    it('should create an instance of TaxClass', () => {
      cy.get(`[data-cy="name"]`).type('Dynamic').should('have.value', 'Dynamic');

      cy.get(`[data-cy="slug"]`).type('Avon Salad Liaison').should('have.value', 'Avon Salad Liaison');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        taxClass = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', taxClassPageUrlPattern);
    });
  });
});
