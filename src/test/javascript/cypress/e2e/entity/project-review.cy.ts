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

describe('ProjectReview e2e test', () => {
  const projectReviewPageUrl = '/project-review';
  const projectReviewPageUrlPattern = new RegExp('/project-review(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const projectReviewSample = {"reviewerName":"ability Bedfordshire","reviewerEmail":"k520Ri@D0-./o5&:","review":"monetizeXXXXXXXXXXXX","rating":16729,"status":"SPAM","reviewerId":11573};

  let projectReview;
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
      body: {"name":"paradigms","slug":"connecting","description":"SecurityXXXXXXXXXXXX","shortDescription":"Personal AvonXXXXXXX","regularPrice":2038,"salePrice":61182,"published":false,"dateCreated":"2022-10-22T07:09:50.477Z","dateModified":"2022-10-22","projectStatus":"SOLD","sharableHash":"Sausages","estimatedBudget":28741},
    }).then(({ body }) => {
      project = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/project-reviews+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-reviews').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-reviews/*').as('deleteEntityRequest');
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
    if (projectReview) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/project-reviews/${projectReview.id}`,
      }).then(() => {
        projectReview = undefined;
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

  it('ProjectReviews menu should load ProjectReviews page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-review');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectReview').should('exist');
    cy.url().should('match', projectReviewPageUrlPattern);
  });

  describe('ProjectReview page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(projectReviewPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProjectReview page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/project-review/new$'));
        cy.getEntityCreateUpdateHeading('ProjectReview');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectReviewPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/project-reviews',
          body: {
            ...projectReviewSample,
            project: project,
          },
        }).then(({ body }) => {
          projectReview = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/project-reviews+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/project-reviews?page=0&size=20>; rel="last",<http://localhost/api/project-reviews?page=0&size=20>; rel="first"',
              },
              body: [projectReview],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(projectReviewPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(projectReviewPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ProjectReview page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('projectReview');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectReviewPageUrlPattern);
      });

      it('edit button click should load edit ProjectReview page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectReview');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectReviewPageUrlPattern);
      });

      it('edit button click should load edit ProjectReview page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectReview');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectReviewPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ProjectReview', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('projectReview').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectReviewPageUrlPattern);

        projectReview = undefined;
      });
    });
  });

  describe('new ProjectReview page', () => {
    beforeEach(() => {
      cy.visit(`${projectReviewPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProjectReview');
    });

    it.skip('should create an instance of ProjectReview', () => {
      cy.get(`[data-cy="reviewerName"]`).type('world-class Account').should('have.value', 'world-class Account');

      cy.get(`[data-cy="reviewerEmail"]`).type('&amp;IS#@o3Y6.)x,f').should('have.value', '&amp;IS#@o3Y6.)x,f');

      cy.get(`[data-cy="review"]`).type('wireless IowaXXXXXXX').should('have.value', 'wireless IowaXXXXXXX');

      cy.get(`[data-cy="rating"]`).type('33130').should('have.value', '33130');

      cy.get(`[data-cy="status"]`).select('UNSPAM');

      cy.get(`[data-cy="reviewerId"]`).type('9450').should('have.value', '9450');

      cy.get(`[data-cy="project"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        projectReview = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', projectReviewPageUrlPattern);
    });
  });
});
