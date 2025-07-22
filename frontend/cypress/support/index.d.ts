/// <reference types="cypress" />

declare namespace Cypress {
  interface Chainable {
    mockApiResponses(): Chainable<Element>;
    mockLogin(email?: string, password?: string, roleId?: number): Chainable<Element>;
    logout(): Chainable<Element>;
  }
}
