describe("Authentication Flow", () => {
  beforeEach(() => {
    // Mock authentication endpoints to avoid creating real users
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

    // Mock check endpoints
    cy.intercept("GET", "**/api/auth/check-email**", {
      statusCode: 200,
      body: { exists: false },
    }).as("mockCheckEmail");

    cy.intercept("GET", "**/api/auth/check-username**", {
      statusCode: 200,
      body: { exists: false },
    }).as("mockCheckUsername");

    cy.visit("/");
  });

  it("should allow user registration and login", () => {
    const testUser = {
      email: "test@example.com",
      name: "TestUser",
      password: "password123",
    };

    // Navigate to registration
    cy.visit("/register");
    cy.contains("Registrar usuario").should("be.visible");

    // Fill registration form
    cy.get('input[type="email"][placeholder="Correo electrónico"]').type(testUser.email);
    cy.get('input[type="text"][placeholder="Usuario"]').type(testUser.name);
    cy.get('input[placeholder="Contraseña"]').first().type(testUser.password);
    cy.get('input[placeholder="Repite la contraseña"]').type(testUser.password);

    // Submit registration
    cy.get('input[type="submit"][value="Registrarse"]').click();

    // Wait for mock registration response
    cy.wait("@mockRegister");

    // Should redirect to events page after successful registration
    cy.url().should("eq", Cypress.config().baseUrl + "/");
    cy.window().then((win) => {
      expect(win.localStorage.getItem("session")).to.not.be.null;
    });
  });

  it("should allow user login with valid credentials", () => {
    const testUser = {
      email: "test@example.com",
      password: "password123",
    };

    // Navigate to login
    cy.visit("/login");
    cy.contains("Iniciar sesión").should("be.visible");

    cy.get('input[type="text"][placeholder="Email"]').type(testUser.email);
    cy.get('input[placeholder="Contraseña"]').type(testUser.password);
    cy.get('input[type="submit"][value="Acceder"]').click();

    // Wait for mock login response
    cy.wait("@mockLogin");

    // Should redirect to events page
    cy.url().should("eq", Cypress.config().baseUrl + "/");
    cy.window().then((win) => {
      expect(win.localStorage.getItem("session")).to.not.be.null;
    });
  });

  it("should show error for invalid login credentials", () => {
    // Mock login error response
    cy.intercept("POST", "**/api/auth/login", {
      statusCode: 401,
      body: { message: "Credenciales inválidas" },
    }).as("mockLoginError");

    cy.visit("/login");

    cy.get('input[type="text"][placeholder="Email"]').type("invalid@example.com");
    cy.get('input[placeholder="Contraseña"]').type("wrongpassword");
    cy.get('input[type="submit"][value="Acceder"]').click();

    // Wait for error response
    cy.wait("@mockLoginError");

    // Should stay on login page
    cy.url().should("include", "/login");
  });

  it("should logout successfully", () => {
    // Start with a mock logged-in state
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

    cy.visit("/");

    // Find and click logout button in header
    cy.contains("Cerrar sesión").click();

    // Verify user is logged out
    cy.window().then((win) => {
      expect(win.localStorage.getItem("session")).to.be.null;
    });

    // Should redirect to login
    cy.url().should("include", "/login");
  });

  it("should redirect to login when accessing protected routes without authentication", () => {
    cy.visit("/profile");
    cy.url().should("include", "/login");

    cy.visit("/admin");
    cy.url().should("include", "/login");

    cy.visit("/organiser");
    cy.url().should("include", "/login");
  });
});
