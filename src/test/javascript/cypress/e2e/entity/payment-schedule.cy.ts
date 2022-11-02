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

describe('PaymentSchedule e2e test', () => {
  const paymentSchedulePageUrl = '/payment-schedule';
  const paymentSchedulePageUrlPattern = new RegExp('/payment-schedule(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const paymentScheduleSample = { dueDate: '2022-10-27T17:57:22.755Z', totalPrice: 49546, status: 'PENDING' };

  let paymentSchedule;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/payment-schedules+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/payment-schedules').as('postEntityRequest');
    cy.intercept('DELETE', '/api/payment-schedules/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (paymentSchedule) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/payment-schedules/${paymentSchedule.id}`,
      }).then(() => {
        paymentSchedule = undefined;
      });
    }
  });

  it('PaymentSchedules menu should load PaymentSchedules page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('payment-schedule');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PaymentSchedule').should('exist');
    cy.url().should('match', paymentSchedulePageUrlPattern);
  });

  describe('PaymentSchedule page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(paymentSchedulePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PaymentSchedule page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/payment-schedule/new$'));
        cy.getEntityCreateUpdateHeading('PaymentSchedule');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentSchedulePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/payment-schedules',
          body: paymentScheduleSample,
        }).then(({ body }) => {
          paymentSchedule = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/payment-schedules+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/payment-schedules?page=0&size=20>; rel="last",<http://localhost/api/payment-schedules?page=0&size=20>; rel="first"',
              },
              body: [paymentSchedule],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(paymentSchedulePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PaymentSchedule page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('paymentSchedule');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentSchedulePageUrlPattern);
      });

      it('edit button click should load edit PaymentSchedule page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PaymentSchedule');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentSchedulePageUrlPattern);
      });

      it('edit button click should load edit PaymentSchedule page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PaymentSchedule');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentSchedulePageUrlPattern);
      });

      it('last delete button click should delete instance of PaymentSchedule', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('paymentSchedule').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentSchedulePageUrlPattern);

        paymentSchedule = undefined;
      });
    });
  });

  describe('new PaymentSchedule page', () => {
    beforeEach(() => {
      cy.visit(`${paymentSchedulePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PaymentSchedule');
    });

    it('should create an instance of PaymentSchedule', () => {
      cy.get(`[data-cy="dueDate"]`).type('2022-10-28T13:11').blur().should('have.value', '2022-10-28T13:11');

      cy.get(`[data-cy="totalPrice"]`).type('42840').should('have.value', '42840');

      cy.get(`[data-cy="remarks"]`).type('niches COMXXXXXXXXXX').should('have.value', 'niches COMXXXXXXXXXX');

      cy.get(`[data-cy="status"]`).select('PLANNED');

      cy.get(`[data-cy="isOverDue"]`).should('not.be.checked');
      cy.get(`[data-cy="isOverDue"]`).click().should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        paymentSchedule = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', paymentSchedulePageUrlPattern);
    });
  });
});
