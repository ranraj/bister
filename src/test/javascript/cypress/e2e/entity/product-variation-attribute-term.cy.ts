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

describe('ProductVariationAttributeTerm e2e test', () => {
  const productVariationAttributeTermPageUrl = '/product-variation-attribute-term';
  const productVariationAttributeTermPageUrlPattern = new RegExp('/product-variation-attribute-term(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productVariationAttributeTermSample = {
    name: 'Web connect',
    slug: 'Generic',
    description: 'virtual payment Forward',
    menuOrder: 81030,
  };

  let productVariationAttributeTerm;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/product-variation-attribute-terms+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-variation-attribute-terms').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-variation-attribute-terms/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (productVariationAttributeTerm) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-variation-attribute-terms/${productVariationAttributeTerm.id}`,
      }).then(() => {
        productVariationAttributeTerm = undefined;
      });
    }
  });

  it('ProductVariationAttributeTerms menu should load ProductVariationAttributeTerms page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-variation-attribute-term');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductVariationAttributeTerm').should('exist');
    cy.url().should('match', productVariationAttributeTermPageUrlPattern);
  });

  describe('ProductVariationAttributeTerm page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productVariationAttributeTermPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductVariationAttributeTerm page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-variation-attribute-term/new$'));
        cy.getEntityCreateUpdateHeading('ProductVariationAttributeTerm');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationAttributeTermPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-variation-attribute-terms',
          body: productVariationAttributeTermSample,
        }).then(({ body }) => {
          productVariationAttributeTerm = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-variation-attribute-terms+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-variation-attribute-terms?page=0&size=20>; rel="last",<http://localhost/api/product-variation-attribute-terms?page=0&size=20>; rel="first"',
              },
              body: [productVariationAttributeTerm],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productVariationAttributeTermPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProductVariationAttributeTerm page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productVariationAttributeTerm');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationAttributeTermPageUrlPattern);
      });

      it('edit button click should load edit ProductVariationAttributeTerm page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductVariationAttributeTerm');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationAttributeTermPageUrlPattern);
      });

      it('edit button click should load edit ProductVariationAttributeTerm page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductVariationAttributeTerm');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationAttributeTermPageUrlPattern);
      });

      it('last delete button click should delete instance of ProductVariationAttributeTerm', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productVariationAttributeTerm').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationAttributeTermPageUrlPattern);

        productVariationAttributeTerm = undefined;
      });
    });
  });

  describe('new ProductVariationAttributeTerm page', () => {
    beforeEach(() => {
      cy.visit(`${productVariationAttributeTermPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductVariationAttributeTerm');
    });

    it('should create an instance of ProductVariationAttributeTerm', () => {
      cy.get(`[data-cy="name"]`).type('orchid').should('have.value', 'orchid');

      cy.get(`[data-cy="slug"]`).type('Cotton Washington Convertible').should('have.value', 'Cotton Washington Convertible');

      cy.get(`[data-cy="description"]`).type('SriXX').should('have.value', 'SriXX');

      cy.get(`[data-cy="menuOrder"]`).type('69848').should('have.value', '69848');

      cy.get(`[data-cy="overRideProductAttribute"]`).should('not.be.checked');
      cy.get(`[data-cy="overRideProductAttribute"]`).click().should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        productVariationAttributeTerm = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productVariationAttributeTermPageUrlPattern);
    });
  });
});
