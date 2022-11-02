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

describe('ProjectActivity e2e test', () => {
  const projectActivityPageUrl = '/project-activity';
  const projectActivityPageUrlPattern = new RegExp('/project-activity(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const projectActivitySample = {"title":"pricing","status":"INPROGRESS"};

  let projectActivity;
  // let project;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/projects',
      body: {"name":"Response back","slug":"invoice","description":"copying Function-based","shortDescription":"withdrawalXXXXXXXXXX","regularPrice":98862,"salePrice":80379,"published":false,"dateCreated":"2022-10-28T00:50:15.519Z","dateModified":"2022-10-27","projectStatus":"NEW","sharableHash":"Cambridgeshire Switchable","estimatedBudget":72936},
    }).then(({ body }) => {
      project = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/project-activities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-activities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-activities/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/projects', {
      statusCode: 200,
      body: [project],
    });

  });
   */

  afterEach(() => {
    if (projectActivity) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/project-activities/${projectActivity.id}`,
      }).then(() => {
        projectActivity = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (project) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/projects/${project.id}`,
      }).then(() => {
        project = undefined;
      });
    }
  });
   */

  it('ProjectActivities menu should load ProjectActivities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-activity');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectActivity').should('exist');
    cy.url().should('match', projectActivityPageUrlPattern);
  });

  describe('ProjectActivity page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(projectActivityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProjectActivity page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/project-activity/new$'));
        cy.getEntityCreateUpdateHeading('ProjectActivity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectActivityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/project-activities',
          body: {
            ...projectActivitySample,
            project: project,
          },
        }).then(({ body }) => {
          projectActivity = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/project-activities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/project-activities?page=0&size=20>; rel="last",<http://localhost/api/project-activities?page=0&size=20>; rel="first"',
              },
              body: [projectActivity],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(projectActivityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(projectActivityPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProjectActivity page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('projectActivity');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectActivityPageUrlPattern);
      });

      it('edit button click should load edit ProjectActivity page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectActivity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectActivityPageUrlPattern);
      });

      it('edit button click should load edit ProjectActivity page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectActivity');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectActivityPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProjectActivity', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('projectActivity').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectActivityPageUrlPattern);

        projectActivity = undefined;
      });
    });
  });

  describe('new ProjectActivity page', () => {
    beforeEach(() => {
      cy.visit(`${projectActivityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProjectActivity');
    });

    it.skip('should create an instance of ProjectActivity', () => {
      cy.get(`[data-cy="title"]`).type('Chips').should('have.value', 'Chips');

      cy.get(`[data-cy="details"]`).type('PesoXXXXXXXXXXXXXXXX').should('have.value', 'PesoXXXXXXXXXXXXXXXX');

      cy.get(`[data-cy="status"]`).select('HOLD');

      cy.get(`[data-cy="project"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        projectActivity = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', projectActivityPageUrlPattern);
    });
  });
});
