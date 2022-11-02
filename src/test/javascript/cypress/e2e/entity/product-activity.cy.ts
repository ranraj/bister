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

describe('ProductActivity e2e test', () => {
  const productActivityPageUrl = '/product-activity';
  const productActivityPageUrlPattern = new RegExp('/product-activity(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const productActivitySample = {"title":"RSS granular","status":"INPROGRESS"};

  let productActivity;
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
      body: {"name":"Frozen Toys","slug":"Mauritius","description":"CheckingXXXXXXXXXXXX","shortDescription":"Handcrafted Practical calculating","regularPrice":99966,"salePrice":42648,"published":true,"dateCreated":"2022-10-27T22:09:37.190Z","dateModified":"2022-10-27","featured":true,"saleStatus":"RESALE","sharableHash":"Group Inverse payment"},
    }).then(({ body }) => {
      product = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/product-activities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-activities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-activities/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/products', {
      statusCode: 200,
      body: [product],
    });

  });
   */

  afterEach(() => {
    if (productActivity) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-activities/${productActivity.id}`,
      }).then(() => {
        productActivity = undefined;
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

  it('ProductActivities menu should load ProductActivities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-activity');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductActivity').should('exist');
    cy.url().should('match', productActivityPageUrlPattern);
  });

  describe('ProductActivity page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productActivityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductActivity page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-activity/new$'));
        cy.getEntityCreateUpdateHeading('ProductActivity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productActivityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-activities',
          body: {
            ...productActivitySample,
            product: product,
          },
        }).then(({ body }) => {
          productActivity = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-activities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-activities?page=0&size=20>; rel="last",<http://localhost/api/product-activities?page=0&size=20>; rel="first"',
              },
              body: [productActivity],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productActivityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(productActivityPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProductActivity page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productActivity');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productActivityPageUrlPattern);
      });

      it('edit button click should load edit ProductActivity page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductActivity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productActivityPageUrlPattern);
      });

      it('edit button click should load edit ProductActivity page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductActivity');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productActivityPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProductActivity', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productActivity').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productActivityPageUrlPattern);

        productActivity = undefined;
      });
    });
  });

  describe('new ProductActivity page', () => {
    beforeEach(() => {
      cy.visit(`${productActivityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductActivity');
    });

    it.skip('should create an instance of ProductActivity', () => {
      cy.get(`[data-cy="title"]`).type('Producer').should('have.value', 'Producer');

      cy.get(`[data-cy="details"]`).type('withdrawal Wooden Mozambique').should('have.value', 'withdrawal Wooden Mozambique');

      cy.get(`[data-cy="status"]`).select('HOLD');

      cy.get(`[data-cy="product"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        productActivity = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productActivityPageUrlPattern);
    });
  });
});
