/// <reference types="cypress" />

// Custom commands for Cypress - Mock version to avoid polluting database

// Command to mock API responses instead of hitting real database
Cypress.Commands.add("mockApiResponses", () => {
  // Mock authentication endpoints
  cy.intercept("POST", "**/api/auth/register", {
    statusCode: 200,
    body: {
      token: "mock-jwt-token",
      userId: 123,
      roleId: 1,
      email: "test@example.com",
      name: "TestUser",
    },
  }).as("mockRegister");

  cy.intercept("POST", "**/api/auth/login", {
    statusCode: 200,
    body: {
      token: "mock-jwt-token",
      userId: 123,
      roleId: 1,
      email: "test@example.com",
      name: "TestUser",
    },
  }).as("mockLogin");

  // Mock check user existence endpoints
  cy.intercept("GET", "**/api/auth/check-email**", {
    statusCode: 200,
    body: { exists: false },
  }).as("mockCheckEmail");

  cy.intercept("GET", "**/api/auth/check-username**", {
    statusCode: 200,
    body: { exists: false },
  }).as("mockCheckUsername");

  // Mock events endpoints
  cy.intercept("GET", "**/api/events**", { fixture: "events.json" }).as("mockEvents");

  // Mock provinces and localities
  cy.intercept("GET", "**/api/provinces**", {
    statusCode: 200,
    body: [
      { id: 1, name: "Madrid" },
      { id: 2, name: "Barcelona" },
      { id: 3, name: "Valencia" },
    ],
  }).as("mockProvinces");

  cy.intercept("GET", "**/api/localities**", {
    statusCode: 200,
    body: [
      { id: 1, name: "Madrid Centro", provinceId: 1 },
      { id: 2, name: "Barcelona Centro", provinceId: 2 },
    ],
  }).as("mockLocalities");
});

// Command to simulate login without hitting database
Cypress.Commands.add("mockLogin", (email, password, roleId) => {
  // Set mock session data in localStorage
  const mockSession = {
    token: "mock-jwt-token",
    userId: 123,
    roleId: roleId || 1,
    email: email || "test@example.com",
    name: "TestUser",
  };

  cy.window().then((win) => {
    win.localStorage.setItem("session", JSON.stringify(mockSession));
  });
});

// Command to logout
Cypress.Commands.add("logout", () => {
  cy.window().then((win) => {
    win.localStorage.removeItem("session");
  });
});
