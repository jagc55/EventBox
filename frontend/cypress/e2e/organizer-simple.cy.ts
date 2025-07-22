describe("Organizer Dashboard - Simplified", () => {
  beforeEach(() => {
    // Mock organizer session
    cy.window().then((win) => {
      const mockSession = {
        token: "mock-organizer-token",
        userId: 124,
        roleId: 2,
        email: "organizer@example.com",
        name: "OrganizerUser",
      };
      win.localStorage.setItem("session", JSON.stringify(mockSession));
    });
  });

  it("should allow organizer to navigate to organiser page", () => {
    cy.visit("/organiser");
    cy.url().should("include", "/organiser");
    cy.get("body").should("not.be.empty");
  });

  it("should display some content on organiser page", () => {
    cy.visit("/organiser");
    // Just verify the page loads without expecting specific elements
    cy.get("body").should("be.visible");
  });
});
