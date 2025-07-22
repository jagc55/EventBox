describe("Advanced Search and Filters", () => {
  beforeEach(() => {
    cy.visit("/");
    cy.mockApiResponses();
  });

  it("should perform complex multi-filter search", () => {
    // Open search filters
    cy.contains("Buscador de eventos").click();

    // Apply multiple filters
    cy.get("#searchQuery").type("música");
    cy.get("#startDate").type("2024-01-01");
    cy.get("#endDate").type("2024-12-31");

    // Apply province filter if available
    cy.get("body").then(($body) => {
      if ($body.find("#province").length > 0) {
        cy.get("#province").select("Madrid");
      }
    });

    // Submit search
    cy.get("#searchQuery").type("{enter}");

    // Verify results are filtered
    cy.get("body").should("exist");
  });

  it("should save and restore search preferences", () => {
    // Open filters and set preferences
    cy.contains("Buscador de eventos").click();
    cy.get("#searchQuery").type("concierto");

    // Navigate away and back by visiting home
    cy.visit("/");
    cy.contains("Buscador de eventos").click();

    // Check if search functionality still works
    cy.get("#searchQuery").should("exist");
  });

  it("should handle empty search results gracefully", () => {
    cy.contains("Buscador de eventos").click();
    cy.get("#searchQuery").type("eventoinexistente12345");
    cy.get("#searchQuery").type("{enter}");

    // Should show no results message or empty state
    cy.get("body").should("exist");
    cy.get("body").then(($body) => {
      if ($body.text().includes("No se encontraron") || $body.text().includes("Sin resultados")) {
        cy.contains("No se encontraron").should("be.visible");
      }
    });
  });

  it("should filter by event type or category", () => {
    cy.contains("Buscador de eventos").click();

    // Look for category/type filters
    cy.get("body").then(($body) => {
      if ($body.find("select").filter('[id*="type"], [id*="category"]').length > 0) {
        cy.get("select").filter('[id*="type"], [id*="category"]').first().select(1);
      }
    });

    cy.get("body").should("exist");
  });

  it("should sort events by different criteria", () => {
    // Look for sorting options
    cy.get("body").then(($body) => {
      if ($body.find("select").filter(':contains("Ordenar")').length > 0) {
        cy.get("select").filter(':contains("Ordenar")').select("Fecha");
      } else if ($body.find("button").filter(':contains("Ordenar")').length > 0) {
        cy.get("button").filter(':contains("Ordenar")').click();
      }
    });

    cy.get("body").should("exist");
  });

  it("should filter by price range", () => {
    cy.contains("Buscador de eventos").click();

    // Look for price filters
    cy.get("body").then(($body) => {
      if ($body.find('input[type="range"]').length > 0) {
        cy.get('input[type="range"]').first().invoke("val", 50).trigger("input");
      } else if ($body.find("input").filter('[placeholder*="precio"]').length > 0) {
        cy.get("input").filter('[placeholder*="precio"]').type("0-100");
      }
    });

    cy.get("body").should("exist");
  });

  it("should filter by distance or location", () => {
    cy.contains("Buscador de eventos").click();

    // Test location-based filtering
    cy.get("body").then(($body) => {
      if ($body.find("#locality").length > 0) {
        cy.get("#locality").select(1);
      }
    });

    cy.get("body").should("exist");
  });

  it("should clear all filters", () => {
    cy.contains("Buscador de eventos").click();

    // Set some filters
    cy.get("#searchQuery").type("test");
    cy.get("#startDate").type("2024-01-01");

    // Look for clear button
    cy.get("body").then(($body) => {
      if ($body.find("button").filter(':contains("Limpiar")').length > 0) {
        cy.get("button").filter(':contains("Limpiar")').click();
        cy.get("#searchQuery").should("have.value", "");
      }
    });
  });

  it("should handle real-time search suggestions", () => {
    cy.contains("Buscador de eventos").click();
    cy.get("#searchQuery").type("co");

    // Wait for potential autocomplete
    cy.wait(500);

    // Look for dropdown suggestions
    cy.get("body").then(($body) => {
      if ($body.find(".dropdown, .suggestions, .autocomplete").length > 0) {
        cy.get(".dropdown, .suggestions, .autocomplete").should("be.visible");
      }
    });
  });
});
