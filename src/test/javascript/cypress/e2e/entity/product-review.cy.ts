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

describe('ProductReview e2e test', () => {
  const productReviewPageUrl = '/product-review';
  const productReviewPageUrlPattern = new RegExp('/product-review(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const productReviewSample = {"reviewerName":"Investor wireless","reviewerEmail":"a\"VPzE@*6IpL.*A","review":"HomeX","rating":69462,"status":"SPAM","reviewerId":15216};

  let productReview;
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
      body: {"name":"system-worthy salmon Louisiana","slug":"index","description":"integrated violetXXX","shortDescription":"Regional ClonedXXXXX","regularPrice":66594,"salePrice":17361,"published":false,"dateCreated":"2022-10-22T23:08:04.257Z","dateModified":"2022-10-22","featured":true,"saleStatus":"RESALE","sharableHash":"navigating"},
    }).then(({ body }) => {
      product = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/product-reviews+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-reviews').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-reviews/*').as('deleteEntityRequest');
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
    if (productReview) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-reviews/${productReview.id}`,
      }).then(() => {
        productReview = undefined;
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

  it('ProductReviews menu should load ProductReviews page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-review');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductReview').should('exist');
    cy.url().should('match', productReviewPageUrlPattern);
  });

  describe('ProductReview page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productReviewPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductReview page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-review/new$'));
        cy.getEntityCreateUpdateHeading('ProductReview');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productReviewPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-reviews',
          body: {
            ...productReviewSample,
            product: product,
          },
        }).then(({ body }) => {
          productReview = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-reviews+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-reviews?page=0&size=20>; rel="last",<http://localhost/api/product-reviews?page=0&size=20>; rel="first"',
              },
              body: [productReview],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productReviewPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(productReviewPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProductReview page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productReview');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productReviewPageUrlPattern);
      });

      it('edit button click should load edit ProductReview page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductReview');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productReviewPageUrlPattern);
      });

      it('edit button click should load edit ProductReview page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductReview');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productReviewPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProductReview', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productReview').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productReviewPageUrlPattern);

        productReview = undefined;
      });
    });
  });

  describe('new ProductReview page', () => {
    beforeEach(() => {
      cy.visit(`${productReviewPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductReview');
    });

    it.skip('should create an instance of ProductReview', () => {
      cy.get(`[data-cy="reviewerName"]`).type('Rand forecast withdrawal').should('have.value', 'Rand forecast withdrawal');

      cy.get(`[data-cy="reviewerEmail"]`).type('7GQ5I@LL&lt;.gK').should('have.value', '7GQ5I@LL&lt;.gK');

      cy.get(`[data-cy="review"]`).type('pixel syndicate').should('have.value', 'pixel syndicate');

      cy.get(`[data-cy="rating"]`).type('61510').should('have.value', '61510');

      cy.get(`[data-cy="status"]`).select('UNTRASH');

      cy.get(`[data-cy="reviewerId"]`).type('15641').should('have.value', '15641');

      cy.get(`[data-cy="product"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        productReview = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productReviewPageUrlPattern);
    });
  });
});
