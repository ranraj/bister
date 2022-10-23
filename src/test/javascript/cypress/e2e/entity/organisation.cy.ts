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

describe('Organisation e2e test', () => {
  const organisationPageUrl = '/organisation';
  const organisationPageUrlPattern = new RegExp('/organisation(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const organisationSample = { name: 'Concrete microchip', description: 'Estate', key: 'Industrial' };

  let organisation;
  let address;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/addresses',
      body: {
        name: 'Switchable Curve Fresh',
        addressLine1: 'deposit Rubber Falkland',
        addressLine2: 'Ohio collaborative Tasty',
        landmark: 'back parsing sexy',
        city: 'Bauchtown',
        state: 'architect Planner',
        country: 'Pitcairn Islands',
        postcode: 'parse silver Switcha',
        latitude: 'Fantastic Investment',
        longitude: 'ivory Awesome Toys',
        addressType: 'NEW_SITE',
      },
    }).then(({ body }) => {
      address = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/organisations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/organisations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/organisations/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/addresses', {
      statusCode: 200,
      body: [address],
    });

    cy.intercept('GET', '/api/business-partners', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (organisation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/organisations/${organisation.id}`,
      }).then(() => {
        organisation = undefined;
      });
    }
  });

  afterEach(() => {
    if (address) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/addresses/${address.id}`,
      }).then(() => {
        address = undefined;
      });
    }
  });

  it('Organisations menu should load Organisations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('organisation');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Organisation').should('exist');
    cy.url().should('match', organisationPageUrlPattern);
  });

  describe('Organisation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(organisationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Organisation page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/organisation/new$'));
        cy.getEntityCreateUpdateHeading('Organisation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', organisationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/organisations',
          body: {
            ...organisationSample,
            address: address,
          },
        }).then(({ body }) => {
          organisation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/organisations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/organisations?page=0&size=20>; rel="last",<http://localhost/api/organisations?page=0&size=20>; rel="first"',
              },
              body: [organisation],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(organisationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Organisation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('organisation');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', organisationPageUrlPattern);
      });

      it('edit button click should load edit Organisation page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Organisation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', organisationPageUrlPattern);
      });

      it('edit button click should load edit Organisation page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Organisation');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', organisationPageUrlPattern);
      });

      it('last delete button click should delete instance of Organisation', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('organisation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', organisationPageUrlPattern);

        organisation = undefined;
      });
    });
  });

  describe('new Organisation page', () => {
    beforeEach(() => {
      cy.visit(`${organisationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Organisation');
    });

    it('should create an instance of Organisation', () => {
      cy.get(`[data-cy="name"]`).type('Avon Table').should('have.value', 'Avon Table');

      cy.get(`[data-cy="description"]`).type('paradigms HTTP').should('have.value', 'paradigms HTTP');

      cy.get(`[data-cy="key"]`).type('1080p 1080p').should('have.value', '1080p 1080p');

      cy.get(`[data-cy="address"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        organisation = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', organisationPageUrlPattern);
    });
  });
});
