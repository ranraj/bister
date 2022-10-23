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

describe('ProjectSpecification e2e test', () => {
  const projectSpecificationPageUrl = '/project-specification';
  const projectSpecificationPageUrlPattern = new RegExp('/project-specification(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const projectSpecificationSample = {"title":"Response Incredible","value":"1080p Hat capacitor"};

  let projectSpecification;
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
      body: {"name":"regional bifurcated facilitate","slug":"expedite","description":"Cotton magenta Up-sized","shortDescription":"turquoiseXXXXXXXXXXX","regularPrice":23952,"salePrice":46495,"published":true,"dateCreated":"2022-10-22T13:55:41.597Z","dateModified":"2022-10-22","projectStatus":"PENDING_APPROVAL","sharableHash":"optical","estimatedBudget":78146},
    }).then(({ body }) => {
      project = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/project-specifications+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-specifications').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-specifications/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/attachments', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/project-specification-groups', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/projects', {
      statusCode: 200,
      body: [project],
    });

  });
   */

  afterEach(() => {
    if (projectSpecification) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/project-specifications/${projectSpecification.id}`,
      }).then(() => {
        projectSpecification = undefined;
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

  it('ProjectSpecifications menu should load ProjectSpecifications page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-specification');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectSpecification').should('exist');
    cy.url().should('match', projectSpecificationPageUrlPattern);
  });

  describe('ProjectSpecification page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(projectSpecificationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProjectSpecification page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/project-specification/new$'));
        cy.getEntityCreateUpdateHeading('ProjectSpecification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/project-specifications',
          body: {
            ...projectSpecificationSample,
            project: project,
          },
        }).then(({ body }) => {
          projectSpecification = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/project-specifications+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/project-specifications?page=0&size=20>; rel="last",<http://localhost/api/project-specifications?page=0&size=20>; rel="first"',
              },
              body: [projectSpecification],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(projectSpecificationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(projectSpecificationPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProjectSpecification page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('projectSpecification');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationPageUrlPattern);
      });

      it('edit button click should load edit ProjectSpecification page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectSpecification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationPageUrlPattern);
      });

      it('edit button click should load edit ProjectSpecification page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectSpecification');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProjectSpecification', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('projectSpecification').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationPageUrlPattern);

        projectSpecification = undefined;
      });
    });
  });

  describe('new ProjectSpecification page', () => {
    beforeEach(() => {
      cy.visit(`${projectSpecificationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProjectSpecification');
    });

    it.skip('should create an instance of ProjectSpecification', () => {
      cy.get(`[data-cy="title"]`).type('Sports Mouse Creek').should('have.value', 'Sports Mouse Creek');

      cy.get(`[data-cy="value"]`).type('Investment').should('have.value', 'Investment');

      cy.get(`[data-cy="description"]`).type('Plaza optimizeXXXXXX').should('have.value', 'Plaza optimizeXXXXXX');

      cy.get(`[data-cy="project"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        projectSpecification = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', projectSpecificationPageUrlPattern);
    });
  });
});
