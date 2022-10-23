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

describe('EnquiryResponse e2e test', () => {
  const enquiryResponsePageUrl = '/enquiry-response';
  const enquiryResponsePageUrlPattern = new RegExp('/enquiry-response(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const enquiryResponseSample = { enquiryResponseType: 'SITE_VISIT' };

  let enquiryResponse;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/enquiry-responses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/enquiry-responses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/enquiry-responses/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (enquiryResponse) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/enquiry-responses/${enquiryResponse.id}`,
      }).then(() => {
        enquiryResponse = undefined;
      });
    }
  });

  it('EnquiryResponses menu should load EnquiryResponses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('enquiry-response');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('EnquiryResponse').should('exist');
    cy.url().should('match', enquiryResponsePageUrlPattern);
  });

  describe('EnquiryResponse page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(enquiryResponsePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create EnquiryResponse page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/enquiry-response/new$'));
        cy.getEntityCreateUpdateHeading('EnquiryResponse');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryResponsePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/enquiry-responses',
          body: enquiryResponseSample,
        }).then(({ body }) => {
          enquiryResponse = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/enquiry-responses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/enquiry-responses?page=0&size=20>; rel="last",<http://localhost/api/enquiry-responses?page=0&size=20>; rel="first"',
              },
              body: [enquiryResponse],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(enquiryResponsePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details EnquiryResponse page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('enquiryResponse');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryResponsePageUrlPattern);
      });

      it('edit button click should load edit EnquiryResponse page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EnquiryResponse');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryResponsePageUrlPattern);
      });

      it('edit button click should load edit EnquiryResponse page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EnquiryResponse');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryResponsePageUrlPattern);
      });

      it('last delete button click should delete instance of EnquiryResponse', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('enquiryResponse').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', enquiryResponsePageUrlPattern);

        enquiryResponse = undefined;
      });
    });
  });

  describe('new EnquiryResponse page', () => {
    beforeEach(() => {
      cy.visit(`${enquiryResponsePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('EnquiryResponse');
    });

    it('should create an instance of EnquiryResponse', () => {
      cy.get(`[data-cy="query"]`).type('Baby Developer real-time').should('have.value', 'Baby Developer real-time');

      cy.get(`[data-cy="details"]`).type('Pound').should('have.value', 'Pound');

      cy.get(`[data-cy="enquiryResponseType"]`).select('CHAT');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        enquiryResponse = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', enquiryResponsePageUrlPattern);
    });
  });
});
