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

describe('Agent e2e test', () => {
  const agentPageUrl = '/agent';
  const agentPageUrlPattern = new RegExp('/agent(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const agentSample = {"name":"Avon Iowa","contactNumber":"Bedfordshire","agentType":"ENGINEER"};

  let agent;
  // let user;
  // let facility;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"Branding","firstName":"Foster","lastName":"Kreiger"},
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/facilities',
      body: {"name":"drive open-source","description":"lavender","facilityType":"DEMO"},
    }).then(({ body }) => {
      facility = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/agents+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/agents').as('postEntityRequest');
    cy.intercept('DELETE', '/api/agents/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/facilities', {
      statusCode: 200,
      body: [facility],
    });

  });
   */

  afterEach(() => {
    if (agent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/agents/${agent.id}`,
      }).then(() => {
        agent = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
    if (facility) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/facilities/${facility.id}`,
      }).then(() => {
        facility = undefined;
      });
    }
  });
   */

  it('Agents menu should load Agents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('agent');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Agent').should('exist');
    cy.url().should('match', agentPageUrlPattern);
  });

  describe('Agent page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(agentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Agent page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/agent/new$'));
        cy.getEntityCreateUpdateHeading('Agent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/agents',
          body: {
            ...agentSample,
            user: user,
            facility: facility,
          },
        }).then(({ body }) => {
          agent = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/agents+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/agents?page=0&size=20>; rel="last",<http://localhost/api/agents?page=0&size=20>; rel="first"',
              },
              body: [agent],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(agentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(agentPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Agent page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('agent');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);
      });

      it('edit button click should load edit Agent page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Agent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);
      });

      it('edit button click should load edit Agent page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Agent');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Agent', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('agent').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', agentPageUrlPattern);

        agent = undefined;
      });
    });
  });

  describe('new Agent page', () => {
    beforeEach(() => {
      cy.visit(`${agentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Agent');
    });

    it.skip('should create an instance of Agent', () => {
      cy.get(`[data-cy="name"]`).type('up').should('have.value', 'up');

      cy.get(`[data-cy="contactNumber"]`).type('withdrawal').should('have.value', 'withdrawal');

      cy.get(`[data-cy="avatarUrl"]`).type('override').should('have.value', 'override');

      cy.get(`[data-cy="agentType"]`).select('COUNSELLOR');

      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="facility"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        agent = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', agentPageUrlPattern);
    });
  });
});
