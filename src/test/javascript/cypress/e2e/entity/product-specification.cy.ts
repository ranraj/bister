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

describe('ProductSpecification e2e test', () => {
  const productSpecificationPageUrl = '/product-specification';
  const productSpecificationPageUrlPattern = new RegExp('/product-specification(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const productSpecificationSample = {"title":"Fully-configurable","value":"magnetic"};

  let productSpecification;
  // let product;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/products',
      body: {"name":"payment","slug":"Ergonomic Investor Home","description":"Rubber Solutions Fantastic","shortDescription":"generate KidsXXXXXXX","regularPrice":82848,"salePrice":31133,"published":false,"dateCreated":"2022-10-27T19:40:02.417Z","dateModified":"2022-10-28","featured":true,"saleStatus":"CLOSED","sharableHash":"challenge Kids Networked"},
    }).then(({ body }) => {
      product = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/product-specifications+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-specifications').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-specifications/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/attachments', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/product-specification-groups', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/products', {
      statusCode: 200,
      body: [product],
    });

  });
   */

  afterEach(() => {
    if (productSpecification) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-specifications/${productSpecification.id}`,
      }).then(() => {
        productSpecification = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (product) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/products/${product.id}`,
      }).then(() => {
        product = undefined;
      });
    }
  });
   */

  it('ProductSpecifications menu should load ProductSpecifications page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-specification');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductSpecification').should('exist');
    cy.url().should('match', productSpecificationPageUrlPattern);
  });

  describe('ProductSpecification page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productSpecificationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductSpecification page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-specification/new$'));
        cy.getEntityCreateUpdateHeading('ProductSpecification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-specifications',
          body: {
            ...productSpecificationSample,
            product: product,
          },
        }).then(({ body }) => {
          productSpecification = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-specifications+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-specifications?page=0&size=20>; rel="last",<http://localhost/api/product-specifications?page=0&size=20>; rel="first"',
              },
              body: [productSpecification],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productSpecificationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(productSpecificationPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProductSpecification page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productSpecification');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationPageUrlPattern);
      });

      it('edit button click should load edit ProductSpecification page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductSpecification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationPageUrlPattern);
      });

      it('edit button click should load edit ProductSpecification page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductSpecification');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProductSpecification', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productSpecification').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationPageUrlPattern);

        productSpecification = undefined;
      });
    });
  });

  describe('new ProductSpecification page', () => {
    beforeEach(() => {
      cy.visit(`${productSpecificationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductSpecification');
    });

    it.skip('should create an instance of ProductSpecification', () => {
      cy.get(`[data-cy="title"]`).type('Product').should('have.value', 'Product');

      cy.get(`[data-cy="value"]`).type('array strategic').should('have.value', 'array strategic');

      cy.get(`[data-cy="description"]`).type('Checking synergies Future').should('have.value', 'Checking synergies Future');

      cy.get(`[data-cy="product"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        productSpecification = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productSpecificationPageUrlPattern);
    });
  });
});
