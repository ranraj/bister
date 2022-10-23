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

describe('ProductSpecificationGroup e2e test', () => {
  const productSpecificationGroupPageUrl = '/product-specification-group';
  const productSpecificationGroupPageUrlPattern = new RegExp('/product-specification-group(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productSpecificationGroupSample = { title: 'Generic Iranian', slug: 'Drive Metrics CSS' };

  let productSpecificationGroup;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/product-specification-groups+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-specification-groups').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-specification-groups/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (productSpecificationGroup) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-specification-groups/${productSpecificationGroup.id}`,
      }).then(() => {
        productSpecificationGroup = undefined;
      });
    }
  });

  it('ProductSpecificationGroups menu should load ProductSpecificationGroups page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-specification-group');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductSpecificationGroup').should('exist');
    cy.url().should('match', productSpecificationGroupPageUrlPattern);
  });

  describe('ProductSpecificationGroup page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productSpecificationGroupPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductSpecificationGroup page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-specification-group/new$'));
        cy.getEntityCreateUpdateHeading('ProductSpecificationGroup');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationGroupPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-specification-groups',
          body: productSpecificationGroupSample,
        }).then(({ body }) => {
          productSpecificationGroup = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-specification-groups+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-specification-groups?page=0&size=20>; rel="last",<http://localhost/api/product-specification-groups?page=0&size=20>; rel="first"',
              },
              body: [productSpecificationGroup],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productSpecificationGroupPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProductSpecificationGroup page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productSpecificationGroup');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationGroupPageUrlPattern);
      });

      it('edit button click should load edit ProductSpecificationGroup page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductSpecificationGroup');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationGroupPageUrlPattern);
      });

      it('edit button click should load edit ProductSpecificationGroup page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductSpecificationGroup');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationGroupPageUrlPattern);
      });

      it('last delete button click should delete instance of ProductSpecificationGroup', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productSpecificationGroup').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productSpecificationGroupPageUrlPattern);

        productSpecificationGroup = undefined;
      });
    });
  });

  describe('new ProductSpecificationGroup page', () => {
    beforeEach(() => {
      cy.visit(`${productSpecificationGroupPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductSpecificationGroup');
    });

    it('should create an instance of ProductSpecificationGroup', () => {
      cy.get(`[data-cy="title"]`).type('Salad').should('have.value', 'Salad');

      cy.get(`[data-cy="slug"]`).type('Shirt').should('have.value', 'Shirt');

      cy.get(`[data-cy="description"]`).type('Sheqel HandmadeXXXXX').should('have.value', 'Sheqel HandmadeXXXXX');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        productSpecificationGroup = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productSpecificationGroupPageUrlPattern);
    });
  });
});
