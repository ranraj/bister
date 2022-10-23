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

describe('Phonenumber e2e test', () => {
  const phonenumberPageUrl = '/phonenumber';
  const phonenumberPageUrlPattern = new RegExp('/phonenumber(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const phonenumberSample = { code: 'XML', contactNumber: 'conglomeration ', phonenumberType: 'OFFICIAL_NUMBER2' };

  let phonenumber;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/phonenumbers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/phonenumbers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/phonenumbers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (phonenumber) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/phonenumbers/${phonenumber.id}`,
      }).then(() => {
        phonenumber = undefined;
      });
    }
  });

  it('Phonenumbers menu should load Phonenumbers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('phonenumber');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Phonenumber').should('exist');
    cy.url().should('match', phonenumberPageUrlPattern);
  });

  describe('Phonenumber page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(phonenumberPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Phonenumber page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/phonenumber/new$'));
        cy.getEntityCreateUpdateHeading('Phonenumber');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', phonenumberPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/phonenumbers',
          body: phonenumberSample,
        }).then(({ body }) => {
          phonenumber = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/phonenumbers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/phonenumbers?page=0&size=20>; rel="last",<http://localhost/api/phonenumbers?page=0&size=20>; rel="first"',
              },
              body: [phonenumber],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(phonenumberPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Phonenumber page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('phonenumber');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', phonenumberPageUrlPattern);
      });

      it('edit button click should load edit Phonenumber page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Phonenumber');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', phonenumberPageUrlPattern);
      });

      it('edit button click should load edit Phonenumber page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Phonenumber');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', phonenumberPageUrlPattern);
      });

      it('last delete button click should delete instance of Phonenumber', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('phonenumber').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', phonenumberPageUrlPattern);

        phonenumber = undefined;
      });
    });
  });

  describe('new Phonenumber page', () => {
    beforeEach(() => {
      cy.visit(`${phonenumberPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Phonenumber');
    });

    it('should create an instance of Phonenumber', () => {
      cy.get(`[data-cy="country"]`).type('Pakistan').should('have.value', 'Pakistan');

      cy.get(`[data-cy="code"]`).type('eyeballs g').should('have.value', 'eyeballs g');

      cy.get(`[data-cy="contactNumber"]`).type('Hill Integratio').should('have.value', 'Hill Integratio');

      cy.get(`[data-cy="phonenumberType"]`).select('OFFICE_SECONDARY');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        phonenumber = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', phonenumberPageUrlPattern);
    });
  });
});
