describe("Smoke Test - EventBox", () => {
  beforeEach(() => {
    // Visitar la página principal antes de cada prueba
    cy.visit("/");
  });

  it("should load the homepage successfully", () => {
    // Verificar que la página carga
    cy.get("body").should("be.visible");

    // Verificar que hay contenido relacionado con eventos (ajustado al texto real)
    cy.contains("Buscador de eventos").should("be.visible");

    // Verificar que el header está presente
    cy.contains("EVENTBOX").should("be.visible");
  });

  it("should navigate to login page", () => {
    cy.visit("/login");

    // Verificar título de la página
    cy.contains("Iniciar sesión").should("be.visible");

    // Verificar elementos del formulario de login (usando selectores reales)
    cy.get('input[type="text"][placeholder="Email"]').should("be.visible");
    cy.get('input[placeholder="Contraseña"]').should("be.visible");
    cy.get('input[type="submit"][value="Acceder"]').should("be.visible");

    // Verificar que estamos en la página de login
    cy.url().should("include", "/login");
  });

  it("should navigate to register page", () => {
    cy.visit("/register");

    // Verificar título de la página
    cy.contains("Registrar usuario").should("be.visible");

    // Verificar elementos del formulario de registro (usando selectores reales)
    cy.get('input[type="email"][placeholder="Correo electrónico"]').should("be.visible");
    cy.get('input[type="text"][placeholder="Usuario"]').should("be.visible");
    cy.get('input[placeholder="Contraseña"]').should("be.visible");
    cy.get('input[placeholder="Repite la contraseña"]').should("be.visible");

    // Verificar que estamos en la página de registro
    cy.url().should("include", "/register");
  });

  it("should have working navigation", () => {
    // Verificar que la navegación funciona
    cy.visit("/");

    // Buscar enlaces de navegación en el header
    cy.contains("Iniciar sesión").click();
    cy.url().should("include", "/login");

    // Volver a home y probar registro
    cy.visit("/");
    cy.contains("Registrarse").click();
    cy.url().should("include", "/register");
  });

  it("should display events list", () => {
    cy.visit("/");

    // Verificar que hay algún contenedor para eventos
    cy.contains("Buscador de eventos").should("be.visible");

    // Verificar que no hay error de carga
    cy.get("body").should("not.contain", "Error");
  });

  it("should be responsive", () => {
    // Probar diferentes tamaños de pantalla
    cy.viewport(1280, 720); // Desktop
    cy.visit("/");
    cy.get("body").should("be.visible");
    cy.contains("EVENTBOX").should("be.visible");

    cy.viewport(768, 1024); // Tablet
    cy.get("body").should("be.visible");

    cy.viewport(375, 667); // Mobile
    cy.get("body").should("be.visible");
    // En mobile debería aparecer el menú hamburguesa
    cy.get("button").contains("☰").should("be.visible");
  });
});
