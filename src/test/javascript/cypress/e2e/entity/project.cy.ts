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

describe('Project e2e test', () => {
  const projectPageUrl = '/project';
  const projectPageUrlPattern = new RegExp('/project(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const projectSample = {
    name: 'capacitor',
    slug: 'web-enabled USB Corporate',
    description: 'Kong backing copying',
    regularPrice: 82807,
    salePrice: 50948,
    published: false,
    dateCreated: '2022-10-22T06:51:47.568Z',
    dateModified: '2022-10-22',
    projectStatus: 'NEW',
    estimatedBudget: 90609,
  };

  let project;
  let address;
  let projectType;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/addresses',
      body: {
        name: 'California Bike Customizable',
        addressLine1: 'Nebraska Handcrafted Fresh',
        addressLine2: 'Dollar solutions solution',
        landmark: 'Franc time-frame',
        city: 'Lolaburgh',
        state: 'Analyst Norway',
        country: 'Ethiopia',
        postcode: 'Steel web-readiness ',
        latitude: 'Corners Alaska Handmade',
        longitude: 'Soft invoice',
        addressType: 'TEMPORARY_RESIDENT',
      },
    }).then(({ body }) => {
      address = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/project-types',
      body: { name: 'Oregon', description: 'Quetzal sexy asymmetric' },
    }).then(({ body }) => {
      projectType = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/projects+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/projects').as('postEntityRequest');
    cy.intercept('DELETE', '/api/projects/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/addresses', {
      statusCode: 200,
      body: [address],
    });

    cy.intercept('GET', '/api/tags', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/project-reviews', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/project-specifications', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/attachments', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/enquiries', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/project-activities', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/project-types', {
      statusCode: 200,
      body: [projectType],
    });

    cy.intercept('GET', '/api/categories', {
      statusCode: 200,
      body: [],
    });
  });

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

  afterEach(() => {
    if (address) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/addresses/${address.id}`,
      }).then(() => {
        address = undefined;
      });
    }
    if (projectType) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/project-types/${projectType.id}`,
      }).then(() => {
        projectType = undefined;
      });
    }
  });

  it('Projects menu should load Projects page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Project').should('exist');
    cy.url().should('match', projectPageUrlPattern);
  });

  describe('Project page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(projectPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Project page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/project/new$'));
        cy.getEntityCreateUpdateHeading('Project');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/projects',
          body: {
            ...projectSample,
            address: address,
            projectType: projectType,
          },
        }).then(({ body }) => {
          project = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/projects+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/projects?page=0&size=20>; rel="last",<http://localhost/api/projects?page=0&size=20>; rel="first"',
              },
              body: [project],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(projectPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Project page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('project');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);
      });

      it('edit button click should load edit Project page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Project');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);
      });

      it('edit button click should load edit Project page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Project');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);
      });

      it('last delete button click should delete instance of Project', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('project').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectPageUrlPattern);

        project = undefined;
      });
    });
  });

  describe('new Project page', () => {
    beforeEach(() => {
      cy.visit(`${projectPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Project');
    });

    it('should create an instance of Project', () => {
      cy.get(`[data-cy="name"]`).type('District').should('have.value', 'District');

      cy.get(`[data-cy="slug"]`).type('interactive turn-key').should('have.value', 'interactive turn-key');

      cy.get(`[data-cy="description"]`).type('Security Nicaragua Avon').should('have.value', 'Security Nicaragua Avon');

      cy.get(`[data-cy="shortDescription"]`).type('Toys SpringsXXXXXXXX').should('have.value', 'Toys SpringsXXXXXXXX');

      cy.get(`[data-cy="regularPrice"]`).type('23070').should('have.value', '23070');

      cy.get(`[data-cy="salePrice"]`).type('48093').should('have.value', '48093');

      cy.get(`[data-cy="published"]`).should('not.be.checked');
      cy.get(`[data-cy="published"]`).click().should('be.checked');

      cy.get(`[data-cy="dateCreated"]`).type('2022-10-22T07:41').blur().should('have.value', '2022-10-22T07:41');

      cy.get(`[data-cy="dateModified"]`).type('2022-10-22').blur().should('have.value', '2022-10-22');

      cy.get(`[data-cy="projectStatus"]`).select('NEW');

      cy.get(`[data-cy="sharableHash"]`).type('Investor Automotive transmitting').should('have.value', 'Investor Automotive transmitting');

      cy.get(`[data-cy="estimatedBudget"]`).type('97395').should('have.value', '97395');

      cy.get(`[data-cy="address"]`).select(1);
      cy.get(`[data-cy="projectType"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        project = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', projectPageUrlPattern);
    });
  });
});
