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

describe('Product e2e test', () => {
  const productPageUrl = '/product';
  const productPageUrlPattern = new RegExp('/product(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productSample = {
    name: 'Communications digital calculate',
    slug: 'parse',
    description: 'lime limeXXXXXXXXXXX',
    shortDescription: 'Handcrafted fresh-thinking Consultant',
    regularPrice: 41970,
    salePrice: 1025,
    published: true,
    dateCreated: '2022-10-28T06:15:00.037Z',
    dateModified: '2022-10-27',
    featured: false,
    saleStatus: 'RESALE',
  };

  let product;
  let taxClass;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/tax-classes',
      body: { name: 'Awesome transmit', slug: 'e-services Handcrafted' },
    }).then(({ body }) => {
      taxClass = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/products+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/products').as('postEntityRequest');
    cy.intercept('DELETE', '/api/products/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/product-variations', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/product-attribute-terms', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/tags', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/product-reviews', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/product-specifications', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/attachments', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/enquiries', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/product-activities', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/projects', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/categories', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/tax-classes', {
      statusCode: 200,
      body: [taxClass],
    });
  });

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

  it('Products menu should load Products page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Product').should('exist');
    cy.url().should('match', productPageUrlPattern);
  });

  describe('Product page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Product page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product/new$'));
        cy.getEntityCreateUpdateHeading('Product');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/products',
          body: {
            ...productSample,
            taxClass: taxClass,
          },
        }).then(({ body }) => {
          product = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/products+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/products?page=0&size=20>; rel="last",<http://localhost/api/products?page=0&size=20>; rel="first"',
              },
              body: [product],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Product page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('product');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);
      });

      it('edit button click should load edit Product page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Product');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);
      });

      it('edit button click should load edit Product page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Product');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);
      });

      it('last delete button click should delete instance of Product', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('product').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productPageUrlPattern);

        product = undefined;
      });
    });
  });

  describe('new Product page', () => {
    beforeEach(() => {
      cy.visit(`${productPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Product');
    });

    it('should create an instance of Product', () => {
      cy.get(`[data-cy="name"]`).type('XSS').should('have.value', 'XSS');

      cy.get(`[data-cy="slug"]`).type('Massachusetts Personal').should('have.value', 'Massachusetts Personal');

      cy.get(`[data-cy="description"]`).type('transition Division monitor').should('have.value', 'transition Division monitor');

      cy.get(`[data-cy="shortDescription"]`).type('SCSI RupeeXXXXXXXXXX').should('have.value', 'SCSI RupeeXXXXXXXXXX');

      cy.get(`[data-cy="regularPrice"]`).type('82960').should('have.value', '82960');

      cy.get(`[data-cy="salePrice"]`).type('25602').should('have.value', '25602');

      cy.get(`[data-cy="published"]`).should('not.be.checked');
      cy.get(`[data-cy="published"]`).click().should('be.checked');

      cy.get(`[data-cy="dateCreated"]`).type('2022-10-27T20:28').blur().should('have.value', '2022-10-27T20:28');

      cy.get(`[data-cy="dateModified"]`).type('2022-10-27').blur().should('have.value', '2022-10-27');

      cy.get(`[data-cy="featured"]`).should('not.be.checked');
      cy.get(`[data-cy="featured"]`).click().should('be.checked');

      cy.get(`[data-cy="saleStatus"]`).select('DELIVERY');

      cy.get(`[data-cy="sharableHash"]`).type('application program').should('have.value', 'application program');

      cy.get(`[data-cy="taxClass"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        product = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productPageUrlPattern);
    });
  });
});
