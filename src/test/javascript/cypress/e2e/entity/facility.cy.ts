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

describe('Facility e2e test', () => {
  const facilityPageUrl = '/facility';
  const facilityPageUrlPattern = new RegExp('/facility(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const facilitySample = {"name":"navigate Saint Greenland","description":"Identity","facilityType":"DEMO"};

  let facility;
  // let address;
  // let user;
  // let organisation;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/addresses',
      body: {"name":"Plastic attitude Pants","addressLine1":"Account","addressLine2":"Developer","landmark":"methodologies","city":"South Noemieshire","state":"Soft web-readiness","country":"Bolivia","postcode":"Money","latitude":"haptic Berkshire Personal","longitude":"Facilitator Future-proofed synthesize","addressType":"PROJECT_SITE"},
    }).then(({ body }) => {
      address = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"Wall Refined Practical","firstName":"Amie","lastName":"Bins"},
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/organisations',
      body: {"name":"feed","description":"Georgia","key":"AI holistic digital"},
    }).then(({ body }) => {
      organisation = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/facilities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/facilities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/facilities/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/addresses', {
      statusCode: 200,
      body: [address],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/organisations', {
      statusCode: 200,
      body: [organisation],
    });

  });
   */

  afterEach(() => {
    if (facility) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/facilities/${facility.id}`,
      }).then(() => {
        facility = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (address) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/addresses/${address.id}`,
      }).then(() => {
        address = undefined;
      });
    }
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
    if (organisation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/organisations/${organisation.id}`,
      }).then(() => {
        organisation = undefined;
      });
    }
  });
   */

  it('Facilities menu should load Facilities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('facility');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Facility').should('exist');
    cy.url().should('match', facilityPageUrlPattern);
  });

  describe('Facility page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(facilityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Facility page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/facility/new$'));
        cy.getEntityCreateUpdateHeading('Facility');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facilityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/facilities',
          body: {
            ...facilitySample,
            address: address,
            user: user,
            organisation: organisation,
          },
        }).then(({ body }) => {
          facility = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/facilities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/facilities?page=0&size=20>; rel="last",<http://localhost/api/facilities?page=0&size=20>; rel="first"',
              },
              body: [facility],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(facilityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(facilityPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Facility page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('facility');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facilityPageUrlPattern);
      });

      it('edit button click should load edit Facility page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Facility');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facilityPageUrlPattern);
      });

      it('edit button click should load edit Facility page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Facility');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facilityPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Facility', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('facility').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facilityPageUrlPattern);

        facility = undefined;
      });
    });
  });

  describe('new Facility page', () => {
    beforeEach(() => {
      cy.visit(`${facilityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Facility');
    });

    it.skip('should create an instance of Facility', () => {
      cy.get(`[data-cy="name"]`).type('Functionality Norway payment').should('have.value', 'Functionality Norway payment');

      cy.get(`[data-cy="description"]`).type('array').should('have.value', 'array');

      cy.get(`[data-cy="facilityType"]`).select('HEAD_OFFICE');

      cy.get(`[data-cy="address"]`).select(1);
      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="organisation"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        facility = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', facilityPageUrlPattern);
    });
  });
});
