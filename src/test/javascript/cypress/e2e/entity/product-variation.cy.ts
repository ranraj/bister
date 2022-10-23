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

describe('ProductVariation e2e test', () => {
  const productVariationPageUrl = '/product-variation';
  const productVariationPageUrlPattern = new RegExp('/product-variation(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const productVariationSample = {"name":"database","description":"GamesXXXXX","regularPrice":4348,"salePrice":80012,"dateOnSaleFrom":"2022-10-22","dateOnSaleTo":"2022-10-22","isDraft":true,"useParentDetails":false,"saleStatus":"DELIVERY"};

  let productVariation;
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
      body: {"name":"withdrawal bypassing","slug":"Handcrafted Solutions","description":"cultivate expediteXX","shortDescription":"PCI BelizeXXXXXXXXXX","regularPrice":9201,"salePrice":81937,"published":false,"dateCreated":"2022-10-22T13:29:31.218Z","dateModified":"2022-10-23","featured":true,"saleStatus":"DELIVERY","sharableHash":"Bacon Principal"},
    }).then(({ body }) => {
      product = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/product-variations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-variations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-variations/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/product-variation-attribute-terms', {
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
    if (productVariation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-variations/${productVariation.id}`,
      }).then(() => {
        productVariation = undefined;
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

  it('ProductVariations menu should load ProductVariations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-variation');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductVariation').should('exist');
    cy.url().should('match', productVariationPageUrlPattern);
  });

  describe('ProductVariation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productVariationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductVariation page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-variation/new$'));
        cy.getEntityCreateUpdateHeading('ProductVariation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-variations',
          body: {
            ...productVariationSample,
            product: product,
          },
        }).then(({ body }) => {
          productVariation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-variations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-variations?page=0&size=20>; rel="last",<http://localhost/api/product-variations?page=0&size=20>; rel="first"',
              },
              body: [productVariation],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productVariationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(productVariationPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProductVariation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productVariation');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationPageUrlPattern);
      });

      it('edit button click should load edit ProductVariation page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductVariation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationPageUrlPattern);
      });

      it('edit button click should load edit ProductVariation page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductVariation');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProductVariation', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productVariation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productVariationPageUrlPattern);

        productVariation = undefined;
      });
    });
  });

  describe('new ProductVariation page', () => {
    beforeEach(() => {
      cy.visit(`${productVariationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductVariation');
    });

    it.skip('should create an instance of ProductVariation', () => {
      cy.get(`[data-cy="assetId"]`).type('deposit').should('have.value', 'deposit');

      cy.get(`[data-cy="name"]`).type('systems Global online').should('have.value', 'systems Global online');

      cy.get(`[data-cy="description"]`).type('Kyat attitude-oriented').should('have.value', 'Kyat attitude-oriented');

      cy.get(`[data-cy="regularPrice"]`).type('49663').should('have.value', '49663');

      cy.get(`[data-cy="salePrice"]`).type('692').should('have.value', '692');

      cy.get(`[data-cy="dateOnSaleFrom"]`).type('2022-10-22').blur().should('have.value', '2022-10-22');

      cy.get(`[data-cy="dateOnSaleTo"]`).type('2022-10-22').blur().should('have.value', '2022-10-22');

      cy.get(`[data-cy="isDraft"]`).should('not.be.checked');
      cy.get(`[data-cy="isDraft"]`).click().should('be.checked');

      cy.get(`[data-cy="useParentDetails"]`).should('not.be.checked');
      cy.get(`[data-cy="useParentDetails"]`).click().should('be.checked');

      cy.get(`[data-cy="saleStatus"]`).select('RESALE');

      cy.get(`[data-cy="product"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        productVariation = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productVariationPageUrlPattern);
    });
  });
});
