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

describe('ProductAttributeTerm e2e test', () => {
  const productAttributeTermPageUrl = '/product-attribute-term';
  const productAttributeTermPageUrlPattern = new RegExp('/product-attribute-term(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const productAttributeTermSample = {"name":"card Concrete ubiquitous","slug":"Creative","description":"Communications Maryland Account","menuOrder":34672};

  let productAttributeTerm;
  // let productAttribute;
  // let product;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/product-attributes',
      body: {"name":"Ghana","slug":"Developer paradigms","type":"Investor Incredible","notes":"Functionality Card","visible":false},
    }).then(({ body }) => {
      productAttribute = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/products',
      body: {"name":"mint","slug":"Argentine Synchronised incubate","description":"Music neural Bahamas","shortDescription":"SSLXXXXXXXXXXXXXXXXX","regularPrice":21972,"salePrice":31979,"published":false,"dateCreated":"2022-10-28T06:42:13.569Z","dateModified":"2022-10-28","featured":true,"saleStatus":"CLOSED","sharableHash":"Plastic Paradigm"},
    }).then(({ body }) => {
      product = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/product-attribute-terms+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-attribute-terms').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-attribute-terms/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/product-attributes', {
      statusCode: 200,
      body: [productAttribute],
    });

    cy.intercept('GET', '/api/products', {
      statusCode: 200,
      body: [product],
    });

  });
   */

  afterEach(() => {
    if (productAttributeTerm) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-attribute-terms/${productAttributeTerm.id}`,
      }).then(() => {
        productAttributeTerm = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (productAttribute) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-attributes/${productAttribute.id}`,
      }).then(() => {
        productAttribute = undefined;
      });
    }
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

  it('ProductAttributeTerms menu should load ProductAttributeTerms page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-attribute-term');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductAttributeTerm').should('exist');
    cy.url().should('match', productAttributeTermPageUrlPattern);
  });

  describe('ProductAttributeTerm page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productAttributeTermPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductAttributeTerm page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-attribute-term/new$'));
        cy.getEntityCreateUpdateHeading('ProductAttributeTerm');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributeTermPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-attribute-terms',
          body: {
            ...productAttributeTermSample,
            productAttribute: productAttribute,
            product: product,
          },
        }).then(({ body }) => {
          productAttributeTerm = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-attribute-terms+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-attribute-terms?page=0&size=20>; rel="last",<http://localhost/api/product-attribute-terms?page=0&size=20>; rel="first"',
              },
              body: [productAttributeTerm],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productAttributeTermPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(productAttributeTermPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProductAttributeTerm page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productAttributeTerm');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributeTermPageUrlPattern);
      });

      it('edit button click should load edit ProductAttributeTerm page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductAttributeTerm');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributeTermPageUrlPattern);
      });

      it('edit button click should load edit ProductAttributeTerm page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductAttributeTerm');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributeTermPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProductAttributeTerm', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productAttributeTerm').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productAttributeTermPageUrlPattern);

        productAttributeTerm = undefined;
      });
    });
  });

  describe('new ProductAttributeTerm page', () => {
    beforeEach(() => {
      cy.visit(`${productAttributeTermPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductAttributeTerm');
    });

    it.skip('should create an instance of ProductAttributeTerm', () => {
      cy.get(`[data-cy="name"]`).type('synthesize enhance').should('have.value', 'synthesize enhance');

      cy.get(`[data-cy="slug"]`).type('reinvent transform deposit').should('have.value', 'reinvent transform deposit');

      cy.get(`[data-cy="description"]`).type('Granite calculating').should('have.value', 'Granite calculating');

      cy.get(`[data-cy="menuOrder"]`).type('90117').should('have.value', '90117');

      cy.get(`[data-cy="productAttribute"]`).select(1);
      cy.get(`[data-cy="product"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        productAttributeTerm = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productAttributeTermPageUrlPattern);
    });
  });
});
