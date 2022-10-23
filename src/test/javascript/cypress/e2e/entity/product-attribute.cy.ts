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

describe('ProductAttribute e2e test', () => {
  const productAttributePageUrl = '/product-attribute';
  const productAttributePageUrlPattern = new RegExp('/product-attribute(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productAttributeSample = {
    name: 'Valleys multi-tasking',
    slug: 'open-source capacitor Identity',
    type: 'Borders Integration monitor',
    notes: 'Franc',
  };

  let productAttribute;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/product-attributes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-attributes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-attributes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (productAttribute) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-attributes/${productAttribute.id}`,
      }).then(() => {
        productAttribute = undefined;
      });
    }
  });

  it('ProductAttributes menu should load ProductAttributes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-attribute');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductAttribute').should('exist');
    cy.url().should('match', productAttributePageUrlPattern);
  });

  describe('ProductAttribute page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productAttributePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductAttribute page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-attribute/new$'));
        cy.getEntityCreateUpdateHeading('ProductAttribute');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-attributes',
          body: productAttributeSample,
        }).then(({ body }) => {
          productAttribute = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-attributes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-attributes?page=0&size=20>; rel="last",<http://localhost/api/product-attributes?page=0&size=20>; rel="first"',
              },
              body: [productAttribute],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productAttributePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProductAttribute page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productAttribute');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributePageUrlPattern);
      });

      it('edit button click should load edit ProductAttribute page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductAttribute');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributePageUrlPattern);
      });

      it('edit button click should load edit ProductAttribute page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductAttribute');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributePageUrlPattern);
      });

      it('last delete button click should delete instance of ProductAttribute', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productAttribute').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributePageUrlPattern);

        productAttribute = undefined;
      });
    });
  });

  describe('new ProductAttribute page', () => {
    beforeEach(() => {
      cy.visit(`${productAttributePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductAttribute');
    });

    it('should create an instance of ProductAttribute', () => {
      cy.get(`[data-cy="name"]`).type('Concrete Russian RAM').should('have.value', 'Concrete Russian RAM');

      cy.get(`[data-cy="slug"]`).type('XSS').should('have.value', 'XSS');

      cy.get(`[data-cy="type"]`).type('CSS').should('have.value', 'CSS');

      cy.get(`[data-cy="notes"]`).type('Soap SQL Security').should('have.value', 'Soap SQL Security');

      cy.get(`[data-cy="visible"]`).should('not.be.checked');
      cy.get(`[data-cy="visible"]`).click().should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        productAttribute = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productAttributePageUrlPattern);
    });
  });
});
