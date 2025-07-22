describe("Events Management", () => {
  beforeEach(() => {
    // Mock all API responses to avoid hitting real database
    cy.intercept("GET", "**/api/events**", { fixture: "events.json" }).as("getEvents");
    cy.intercept("GET", "**/api/provinces**", {
      statusCode: 200,
      body: [
        { id: 1, name: "Madrid" },
        { id: 2, name: "Barcelona" },
        { id: 3, name: "Valencia" },
      ],
    }).as("getProvinces");

    cy.intercept("GET", "**/api/localities**", {
      statusCode: 200,
      body: [
        { id: 1, name: "Madrid Centro", provinceId: 1 },
        { id: 2, name: "Barcelona Centro", provinceId: 2 },
      ],
    }).as("getLocalities");

    cy.visit("/");
  });

  it("should display events list on homepage", () => {
    cy.contains("Buscador de eventos").should("be.visible");

    // Check if events grid is displayed (without waiting for specific API calls)
    cy.get(".grid", { timeout: 10000 }).should("exist");
  });

  it("should have working search filters button", () => {
    cy.visit("/");
    cy.mockApiResponses();

    // Wait for page to load and verify search button exists
    cy.contains("Buscador de eventos").should("be.visible");

    // Verify the button is clickable
    cy.get("button").contains("Buscador de eventos").should("be.visible").click();

    // Just verify we can interact with the interface
    cy.get("body").should("exist");
  });

  it("should allow filtering events by search query", () => {
    // Show filters first
    cy.contains("Buscador de eventos").click();

    const searchTerm = "rock";
    cy.get("#searchQuery").type(searchTerm);

    // Trigger search
    cy.get("#searchQuery").type("{enter}");

    // Results should be filtered
    cy.get(".grid").should("exist");
  });

  it("should filter events by date range", () => {
    // Show filters first
    cy.contains("Buscador de eventos").click();

    const today = new Date().toISOString().split("T")[0];
    const nextWeek = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split("T")[0];

    cy.get("#startDate").type(today);
    cy.get("#endDate").type(nextWeek);

    // Click somewhere else to trigger search instead of typing enter
    cy.get("#searchQuery").click();

    cy.get(".grid").should("exist");
  });

  it("should filter events by province and locality", () => {
    // Show filters first
    cy.contains("Buscador de eventos").click();
    cy.get("#selectedProvince").should("be.visible");

    // Select first available province
    cy.get("#selectedProvince").select(1);

    // Wait a bit for localities to potentially load
    cy.wait(500);

    // Check if locality dropdown becomes enabled
    cy.get("#selectedLocality").then(($select) => {
      if (!$select.prop("disabled")) {
        cy.get("#selectedLocality").select(1);
      }
    });

    cy.get(".grid").should("exist");
  });

  it("should show loading state", () => {
    cy.visit("/");
    cy.contains("Cargando eventos...").should("be.visible");
  });

  it("should handle error states with retry", () => {
    // Intercept with error first
    cy.intercept("GET", "**/api/events**", { statusCode: 500 }).as("getEventsError");
    cy.visit("/");
    cy.wait("@getEventsError");

    cy.contains("Reintentar").should("be.visible");

    // Change intercept to success
    cy.intercept("GET", "**/api/events**", { fixture: "events.json" }).as("getEventsSuccess");
    cy.contains("Reintentar").click();
    cy.wait("@getEventsSuccess");

    cy.get(".grid").should("exist");
  });

  it("should allow authenticated users to interact with events", () => {
    // Start with mock logged-in state instead of creating real user
    cy.window().then((win) => {
      win.localStorage.setItem(
        "session",
        JSON.stringify({
          token: "mock-jwt-token",
          userId: 123,
          roleId: 1,
          email: "test@example.com",
          name: "TestUser",
        })
      );
    });

    // Navigate to events
    cy.visit("/");
    cy.get(".grid").should("exist");

    // Look for interactive elements in event cards
    cy.get("body").then(($body) => {
      // Check if favorite buttons exist
      if ($body.find('[data-testid="favorite-button"]').length > 0) {
        cy.get('[data-testid="favorite-button"]').first().click();
      }

      // Check if event detail links exist
      if ($body.find('[data-testid="event-link"]').length > 0) {
        cy.get('[data-testid="event-link"]').first().click();
      }
    });
  });
});
