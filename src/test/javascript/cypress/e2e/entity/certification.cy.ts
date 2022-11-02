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

describe('Certification e2e test', () => {
  const certificationPageUrl = '/certification';
  const certificationPageUrlPattern = new RegExp('/certification(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const certificationSample = {
    name: 'viral',
    authority: 'Computers silver',
    status: 'INITIATED',
    createdBy: 38119,
    createdAt: '2022-10-27T21:40:34.935Z',
  };

  let certification;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/certifications+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/certifications').as('postEntityRequest');
    cy.intercept('DELETE', '/api/certifications/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (certification) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/certifications/${certification.id}`,
      }).then(() => {
        certification = undefined;
      });
    }
  });

  it('Certifications menu should load Certifications page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('certification');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Certification').should('exist');
    cy.url().should('match', certificationPageUrlPattern);
  });

  describe('Certification page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(certificationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Certification page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/certification/new$'));
        cy.getEntityCreateUpdateHeading('Certification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', certificationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/certifications',
          body: certificationSample,
        }).then(({ body }) => {
          certification = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/certifications+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/certifications?page=0&size=20>; rel="last",<http://localhost/api/certifications?page=0&size=20>; rel="first"',
              },
              body: [certification],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(certificationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Certification page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('certification');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', certificationPageUrlPattern);
      });

      it('edit button click should load edit Certification page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Certification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', certificationPageUrlPattern);
      });

      it('edit button click should load edit Certification page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Certification');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', certificationPageUrlPattern);
      });

      it('last delete button click should delete instance of Certification', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('certification').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', certificationPageUrlPattern);

        certification = undefined;
      });
    });
  });

  describe('new Certification page', () => {
    beforeEach(() => {
      cy.visit(`${certificationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Certification');
    });

    it('should create an instance of Certification', () => {
      cy.get(`[data-cy="name"]`).type('North').should('have.value', 'North');

      cy.get(`[data-cy="slug"]`).type('Incredible white complexity').should('have.value', 'Incredible white complexity');

      cy.get(`[data-cy="authority"]`).type('primary Tennessee seamless').should('have.value', 'primary Tennessee seamless');

      cy.get(`[data-cy="status"]`).select('REJECTED');

      cy.get(`[data-cy="projectId"]`).type('38972').should('have.value', '38972');

      cy.get(`[data-cy="prodcut"]`).type('32452').should('have.value', '32452');

      cy.get(`[data-cy="orgId"]`).type('62886').should('have.value', '62886');

      cy.get(`[data-cy="facitlityId"]`).type('53454').should('have.value', '53454');

      cy.get(`[data-cy="createdBy"]`).type('63209').should('have.value', '63209');

      cy.get(`[data-cy="createdAt"]`).type('2022-10-28T00:27').blur().should('have.value', '2022-10-28T00:27');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        certification = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', certificationPageUrlPattern);
    });
  });
});
