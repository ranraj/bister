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

describe('BusinessPartner e2e test', () => {
  const businessPartnerPageUrl = '/business-partner';
  const businessPartnerPageUrlPattern = new RegExp('/business-partner(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const businessPartnerSample = { name: 'Towels', description: 'COMXX', key: 'Fantastic' };

  let businessPartner;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/business-partners+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/business-partners').as('postEntityRequest');
    cy.intercept('DELETE', '/api/business-partners/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (businessPartner) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/business-partners/${businessPartner.id}`,
      }).then(() => {
        businessPartner = undefined;
      });
    }
  });

  it('BusinessPartners menu should load BusinessPartners page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('business-partner');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BusinessPartner').should('exist');
    cy.url().should('match', businessPartnerPageUrlPattern);
  });

  describe('BusinessPartner page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(businessPartnerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BusinessPartner page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/business-partner/new$'));
        cy.getEntityCreateUpdateHeading('BusinessPartner');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPartnerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/business-partners',
          body: businessPartnerSample,
        }).then(({ body }) => {
          businessPartner = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/business-partners+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/business-partners?page=0&size=20>; rel="last",<http://localhost/api/business-partners?page=0&size=20>; rel="first"',
              },
              body: [businessPartner],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(businessPartnerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BusinessPartner page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('businessPartner');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPartnerPageUrlPattern);
      });

      it('edit button click should load edit BusinessPartner page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BusinessPartner');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPartnerPageUrlPattern);
      });

      it('edit button click should load edit BusinessPartner page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BusinessPartner');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPartnerPageUrlPattern);
      });

      it('last delete button click should delete instance of BusinessPartner', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('businessPartner').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPartnerPageUrlPattern);

        businessPartner = undefined;
      });
    });
  });

  describe('new BusinessPartner page', () => {
    beforeEach(() => {
      cy.visit(`${businessPartnerPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BusinessPartner');
    });

    it('should create an instance of BusinessPartner', () => {
      cy.get(`[data-cy="name"]`).type('Hungary compress').should('have.value', 'Hungary compress');

      cy.get(`[data-cy="description"]`).type('Borders Division').should('have.value', 'Borders Division');

      cy.get(`[data-cy="key"]`).type('purple').should('have.value', 'purple');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        businessPartner = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', businessPartnerPageUrlPattern);
    });
  });
});
