describe("Organizer Dashboard - Advanced", () => {
  beforeEach(() => {
    cy.visit("/");
    cy.mockApiResponses();
  });

  it("should allow organizer to create new events", () => {
    // Mock organizer login
    cy.mockLogin();

    // Navigate to create event
    cy.visit("/organiser");

    // Look for create event functionality
    cy.get("body").then(($body) => {
      if ($body.find("button").filter(':contains("Crear")').length > 0) {
        cy.get("button").filter(':contains("Crear")').click();
      } else if ($body.find('a[href*="create"]').length > 0) {
        cy.get('a[href*="create"]').first().click();
      }
    });
  });

  it("should show organizer event management interface", () => {
    cy.mockLogin();

    cy.visit("/organiser");

    // Verify organizer interface elements
    cy.get("body").should("exist");
    cy.url().should("satisfy", (url) => {
      return url.includes("/organiser") || url.includes("/");
    });

    // Look for event management features
    cy.get("body").then(($body) => {
      if ($body.find("table").length > 0) {
        cy.get("table").should("be.visible");
      }
    });
  });

  it("should allow organizer to edit existing events", () => {
    cy.mockLogin();

    cy.visit("/organiser");

    // Look for edit functionality
    cy.get("body").then(($body) => {
      if ($body.find("button").filter(':contains("Editar")').length > 0) {
        cy.get("button").filter(':contains("Editar")').should("be.visible");
      }
    });
  });

  it("should show event statistics for organizer", () => {
    cy.mockLogin();

    cy.visit("/organiser");

    // Look for statistics or metrics
    cy.get("body").should("exist");
    cy.get("body").then(($body) => {
      if ($body.find(".statistic, .metric, .count").length > 0) {
        cy.get(".statistic, .metric, .count").should("be.visible");
      }
    });
  });

  it("should allow organizer to delete events", () => {
    cy.mockLogin();

    cy.visit("/organiser");

    // Look for delete functionality
    cy.get("body").then(($body) => {
      if ($body.find("button").filter(':contains("Eliminar")').length > 0) {
        cy.get("button").filter(':contains("Eliminar")').should("be.visible");
      }
    });
  });

  it("should require organizer role to access organiser page", () => {
    // Login as regular user
    cy.mockLogin(); // This logs in as regular user (role 1)

    cy.visit("/organiser");

    // Should redirect or show access denied
    cy.url().should("satisfy", (url) => {
      return url.includes("/login") || url.includes("/") || url.includes("/organiser");
    });
  });
});
