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

describe('Attachment e2e test', () => {
  const attachmentPageUrl = '/attachment';
  const attachmentPageUrlPattern = new RegExp('/attachment(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const attachmentSample = { name: 'Health' };

  let attachment;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/attachments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/attachments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/attachments/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (attachment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/attachments/${attachment.id}`,
      }).then(() => {
        attachment = undefined;
      });
    }
  });

  it('Attachments menu should load Attachments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('attachment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Attachment').should('exist');
    cy.url().should('match', attachmentPageUrlPattern);
  });

  describe('Attachment page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(attachmentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Attachment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/attachment/new$'));
        cy.getEntityCreateUpdateHeading('Attachment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', attachmentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/attachments',
          body: attachmentSample,
        }).then(({ body }) => {
          attachment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/attachments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/attachments?page=0&size=20>; rel="last",<http://localhost/api/attachments?page=0&size=20>; rel="first"',
              },
              body: [attachment],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(attachmentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Attachment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('attachment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', attachmentPageUrlPattern);
      });

      it('edit button click should load edit Attachment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Attachment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', attachmentPageUrlPattern);
      });

      it('edit button click should load edit Attachment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Attachment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', attachmentPageUrlPattern);
      });

      it('last delete button click should delete instance of Attachment', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('attachment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', attachmentPageUrlPattern);

        attachment = undefined;
      });
    });
  });

  describe('new Attachment page', () => {
    beforeEach(() => {
      cy.visit(`${attachmentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Attachment');
    });

    it('should create an instance of Attachment', () => {
      cy.get(`[data-cy="name"]`).type('Handmade').should('have.value', 'Handmade');

      cy.get(`[data-cy="description"]`).type('Configuration RealXX').should('have.value', 'Configuration RealXX');

      cy.get(`[data-cy="attachmentType"]`).select('LINK');

      cy.get(`[data-cy="link"]`).type('Ranch Accounts silver').should('have.value', 'Ranch Accounts silver');

      cy.get(`[data-cy="isApprovalNeeded"]`).should('not.be.checked');
      cy.get(`[data-cy="isApprovalNeeded"]`).click().should('be.checked');

      cy.get(`[data-cy="approvalStatus"]`).select('APPROVED');

      cy.get(`[data-cy="approvedBy"]`).type('37949').should('have.value', '37949');

      cy.get(`[data-cy="attachmentSourceType"]`).select('USER');

      cy.get(`[data-cy="createdBy"]`).type('88156').should('have.value', '88156');

      cy.get(`[data-cy="customerId"]`).type('18283').should('have.value', '18283');

      cy.get(`[data-cy="agentId"]`).type('76197').should('have.value', '76197');

      cy.get(`[data-cy="attachmentVisibilityType"]`).select('PUBLIC');

      cy.get(`[data-cy="originalFilename"]`).type('Legacy Handmade firewall').should('have.value', 'Legacy Handmade firewall');

      cy.get(`[data-cy="extension"]`).type('mesh New').should('have.value', 'mesh New');

      cy.get(`[data-cy="sizeInBytes"]`).type('80128').should('have.value', '80128');

      cy.get(`[data-cy="sha256"]`).type('Legacy wireless').should('have.value', 'Legacy wireless');

      cy.get(`[data-cy="contentType"]`).type('e-business').should('have.value', 'e-business');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        attachment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', attachmentPageUrlPattern);
    });
  });
});
