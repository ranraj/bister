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

describe('Notification e2e test', () => {
  const notificationPageUrl = '/notification';
  const notificationPageUrlPattern = new RegExp('/notification(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const notificationSample = {"date":"2022-10-27T19:42:55.537Z","sentDate":"2022-10-28T10:15:18.230Z","read":true,"notificationSourceType":"PROMOTION","notificationMode":"SMS"};

  let notification;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"Dynamic connect Personal","firstName":"Shea","lastName":"Jacobs"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/notifications+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/notifications').as('postEntityRequest');
    cy.intercept('DELETE', '/api/notifications/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/agents', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (notification) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/notifications/${notification.id}`,
      }).then(() => {
        notification = undefined;
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
  });
   */

  it('Notifications menu should load Notifications page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('notification');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Notification').should('exist');
    cy.url().should('match', notificationPageUrlPattern);
  });

  describe('Notification page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(notificationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Notification page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/notification/new$'));
        cy.getEntityCreateUpdateHeading('Notification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/notifications',
          body: {
            ...notificationSample,
            user: user,
          },
        }).then(({ body }) => {
          notification = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/notifications+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/notifications?page=0&size=20>; rel="last",<http://localhost/api/notifications?page=0&size=20>; rel="first"',
              },
              body: [notification],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(notificationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(notificationPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Notification page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('notification');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);
      });

      it('edit button click should load edit Notification page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Notification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);
      });

      it('edit button click should load edit Notification page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Notification');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Notification', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('notification').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);

        notification = undefined;
      });
    });
  });

  describe('new Notification page', () => {
    beforeEach(() => {
      cy.visit(`${notificationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Notification');
    });

    it.skip('should create an instance of Notification', () => {
      cy.get(`[data-cy="date"]`).type('2022-10-27T17:20').blur().should('have.value', '2022-10-27T17:20');

      cy.get(`[data-cy="details"]`).type('firewall multimedia').should('have.value', 'firewall multimedia');

      cy.get(`[data-cy="sentDate"]`).type('2022-10-28T03:00').blur().should('have.value', '2022-10-28T03:00');

      cy.get(`[data-cy="googleNotificationId"]`).type('multi-byte teal').should('have.value', 'multi-byte teal');

      cy.get(`[data-cy="whatsappNotificationId"]`)
        .type('clear-thinking Exclusive Florida')
        .should('have.value', 'clear-thinking Exclusive Florida');

      cy.get(`[data-cy="smsNotificationId"]`).type('Frozen').should('have.value', 'Frozen');

      cy.get(`[data-cy="productId"]`).type('85886').should('have.value', '85886');

      cy.get(`[data-cy="projectId"]`).type('33494').should('have.value', '33494');

      cy.get(`[data-cy="scheduleId"]`).type('59781').should('have.value', '59781');

      cy.get(`[data-cy="promotionId"]`).type('8857').should('have.value', '8857');

      cy.get(`[data-cy="read"]`).should('not.be.checked');
      cy.get(`[data-cy="read"]`).click().should('be.checked');

      cy.get(`[data-cy="notificationSourceType"]`).select('PROMOTION');

      cy.get(`[data-cy="notificationType"]`).select('PHYSICAL');

      cy.get(`[data-cy="notificationMode"]`).select('COURIER');

      cy.get(`[data-cy="user"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        notification = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', notificationPageUrlPattern);
    });
  });
});
