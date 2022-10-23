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

describe('ProjectSpecificationGroup e2e test', () => {
  const projectSpecificationGroupPageUrl = '/project-specification-group';
  const projectSpecificationGroupPageUrlPattern = new RegExp('/project-specification-group(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const projectSpecificationGroupSample = { title: 'Nakfa Bike Applications' };

  let projectSpecificationGroup;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/project-specification-groups+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-specification-groups').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-specification-groups/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (projectSpecificationGroup) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/project-specification-groups/${projectSpecificationGroup.id}`,
      }).then(() => {
        projectSpecificationGroup = undefined;
      });
    }
  });

  it('ProjectSpecificationGroups menu should load ProjectSpecificationGroups page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-specification-group');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectSpecificationGroup').should('exist');
    cy.url().should('match', projectSpecificationGroupPageUrlPattern);
  });

  describe('ProjectSpecificationGroup page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(projectSpecificationGroupPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProjectSpecificationGroup page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/project-specification-group/new$'));
        cy.getEntityCreateUpdateHeading('ProjectSpecificationGroup');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationGroupPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/project-specification-groups',
          body: projectSpecificationGroupSample,
        }).then(({ body }) => {
          projectSpecificationGroup = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/project-specification-groups+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/project-specification-groups?page=0&size=20>; rel="last",<http://localhost/api/project-specification-groups?page=0&size=20>; rel="first"',
              },
              body: [projectSpecificationGroup],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(projectSpecificationGroupPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProjectSpecificationGroup page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('projectSpecificationGroup');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationGroupPageUrlPattern);
      });

      it('edit button click should load edit ProjectSpecificationGroup page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectSpecificationGroup');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationGroupPageUrlPattern);
      });

      it('edit button click should load edit ProjectSpecificationGroup page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProjectSpecificationGroup');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationGroupPageUrlPattern);
      });

      it('last delete button click should delete instance of ProjectSpecificationGroup', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('projectSpecificationGroup').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSpecificationGroupPageUrlPattern);

        projectSpecificationGroup = undefined;
      });
    });
  });

  describe('new ProjectSpecificationGroup page', () => {
    beforeEach(() => {
      cy.visit(`${projectSpecificationGroupPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProjectSpecificationGroup');
    });

    it('should create an instance of ProjectSpecificationGroup', () => {
      cy.get(`[data-cy="title"]`).type('Fiji').should('have.value', 'Fiji');

      cy.get(`[data-cy="slug"]`).type('Pants Small salmon').should('have.value', 'Pants Small salmon');

      cy.get(`[data-cy="description"]`).type('Springs Mouse').should('have.value', 'Springs Mouse');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        projectSpecificationGroup = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', projectSpecificationGroupPageUrlPattern);
    });
  });
});
