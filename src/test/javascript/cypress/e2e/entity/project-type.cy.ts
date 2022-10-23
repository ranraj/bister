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

describe('ProjectType e2e test', () => {
  const projectTypePageUrl = '/project-type';
  const projectTypePageUrlPattern = new RegExp('/project-type(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const projectTypeSample = { name: 'help-desk Buckinghamshire clicks-and-mortar' };

  let projectType;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/project-types+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-types').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-types/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (projectType) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/project-types/${projectType.id}`,
      }).then(() => {
        projectType = undefined;
      });
    }
  });

  it('ProjectTypes menu should load ProjectTypes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-type');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectType').should('exist');
    cy.url().should('match', projectTypePageUrlPattern);
  });

  describe('ProjectType page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(projectTypePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProjectType page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/project-type/new$'));
        cy.getEntityCreateUpdateHeading('ProjectType');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectTypePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/project-types',
          body: projectTypeSample,
        }).then(({ body }) => {
          projectType = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/project-types+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/project-types?page=0&size=20>; rel="last",<http://localhost/api/project-types?page=0&size=20>; rel="first"',
              },
              body: [projectType],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(projectTypePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProjectType page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('projectType');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectTypePageUrlPattern);
      });

      it('edit button click should load edit ProjectType page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectType');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectTypePageUrlPattern);
      });

      it('edit button click should load edit ProjectType page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectType');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectTypePageUrlPattern);
      });

      it('last delete button click should delete instance of ProjectType', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('projectType').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectTypePageUrlPattern);

        projectType = undefined;
      });
    });
  });

  describe('new ProjectType page', () => {
    beforeEach(() => {
      cy.visit(`${projectTypePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProjectType');
    });

    it('should create an instance of ProjectType', () => {
      cy.get(`[data-cy="name"]`).type('sensor web-readiness').should('have.value', 'sensor web-readiness');

      cy.get(`[data-cy="description"]`).type('indigoXXXX').should('have.value', 'indigoXXXX');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        projectType = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', projectTypePageUrlPattern);
    });
  });
});
