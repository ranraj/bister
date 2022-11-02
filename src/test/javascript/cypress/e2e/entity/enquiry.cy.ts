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

describe('Enquiry e2e test', () => {
  const enquiryPageUrl = '/enquiry';
  const enquiryPageUrlPattern = new RegExp('/enquiry(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const enquirySample = { raisedDate: '2022-10-28T14:42:18.122Z', subject: 'program', enquiryType: 'CONSULTATION' };

  let enquiry;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/enquiries+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/enquiries').as('postEntityRequest');
    cy.intercept('DELETE', '/api/enquiries/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (enquiry) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/enquiries/${enquiry.id}`,
      }).then(() => {
        enquiry = undefined;
      });
    }
  });

  it('Enquiries menu should load Enquiries page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('enquiry');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Enquiry').should('exist');
    cy.url().should('match', enquiryPageUrlPattern);
  });

  describe('Enquiry page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(enquiryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Enquiry page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/enquiry/new$'));
        cy.getEntityCreateUpdateHeading('Enquiry');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/enquiries',
          body: enquirySample,
        }).then(({ body }) => {
          enquiry = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/enquiries+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/enquiries?page=0&size=20>; rel="last",<http://localhost/api/enquiries?page=0&size=20>; rel="first"',
              },
              body: [enquiry],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(enquiryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Enquiry page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('enquiry');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryPageUrlPattern);
      });

      it('edit button click should load edit Enquiry page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Enquiry');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryPageUrlPattern);
      });

      it('edit button click should load edit Enquiry page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Enquiry');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryPageUrlPattern);
      });

      it('last delete button click should delete instance of Enquiry', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('enquiry').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryPageUrlPattern);

        enquiry = undefined;
      });
    });
  });

  describe('new Enquiry page', () => {
    beforeEach(() => {
      cy.visit(`${enquiryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Enquiry');
    });

    it('should create an instance of Enquiry', () => {
      cy.get(`[data-cy="raisedDate"]`).type('2022-10-28T10:06').blur().should('have.value', '2022-10-28T10:06');

      cy.get(`[data-cy="subject"]`).type('virtual New compressing').should('have.value', 'virtual New compressing');

      cy.get(`[data-cy="details"]`).type('Berkshire holistic Fresh').should('have.value', 'Berkshire holistic Fresh');

      cy.get(`[data-cy="lastResponseDate"]`).type('2022-10-27T23:34').blur().should('have.value', '2022-10-27T23:34');

      cy.get(`[data-cy="lastResponseId"]`).type('38048').should('have.value', '38048');

      cy.get(`[data-cy="enquiryType"]`).select('SERVICE');

      cy.get(`[data-cy="status"]`).select('CLOSED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        enquiry = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', enquiryPageUrlPattern);
    });
  });
});
