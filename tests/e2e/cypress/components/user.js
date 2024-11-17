import { DEFAULT_PASSWORD } from "../constants";

const baseUrl = Cypress.config("baseUrl");

export function register(username) {
    const email = `${username}@example.com`;
    cy.visit("/register");
    cy.get(".container h2.text-center").should("have.text", "Sign Up");
    cy.get("#username").type(username);
    cy.get("#email").type(email);
    cy.get("#first_name").type("Kiwi");
    cy.get("#last_name").type("Tui");
    cy.get("#location").type("Christchurch");
    cy.get("#password").type(DEFAULT_PASSWORD);
    cy.get("#password2").type(DEFAULT_PASSWORD);
    cy.contains(".btn", "Sign Up").click();
    cy.get("#alert-container .alert").should("contain.text", "You have successfully registered!");
}

export function login(username, password) {
    cy.visit("/login");
    cy.contains("a", "Login").click();
    cy.get("#username").clear();
    cy.get("#username").type(username);
    cy.get("#password").clear();
    cy.get("#password").type(password);
    cy.contains(".btn", "Sign in").click();
    cy.get("#navbarDropdown .profile-small").should("be.visible");
}

export function logout() {
    cy.url().should("contains", `${baseUrl}/`);
    cy.get("#navbarDropdown .profile-small").click();
    cy.get(".dropdown-menu").find(".dropdown-item").contains("Logout").click();
    cy.url().should("contains", `${baseUrl}/`);
    cy.get("#navbarDropdown .profile-small").should("not.exist");
}

export function create_user(username, role) {
    const email = `${username}@example.com`;
    cy.visit("/users");
    cy.get("h2").contains("User Management Page").should("be.visible");

    cy.get("a.btn-success").contains("Create user").click();
    cy.get("h2").contains("Create User").should("be.visible");

    cy.get("#username").type(username);
    cy.get("#email").type(email);
    cy.get("#role").select(role);
    cy.get("#first_name").type("Kiwi");
    cy.get("#last_name").type("Tui");
    cy.get("#location").type("Christchurch");
    cy.get("#password").type(DEFAULT_PASSWORD);
    cy.get("#password2").type(DEFAULT_PASSWORD);
    cy.get(".btn").contains("Create User").click();

    cy.get(".alert-success").contains(`${username} have successfully created!`).should("be.visible");
}
