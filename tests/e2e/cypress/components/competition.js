import { randomId } from "../utils";
import { getDateAfterDays } from "../utils";

export function create_competition(competition_name) {
    const start_date = getDateAfterDays(1);
    const end_date = getDateAfterDays(30);
    cy.visit("/competitions");
    cy.get(".competition-card").should("be.visible");

    cy.get("a.btn").contains("Create a Competition").click();
    cy.get("form").should("be.visible");

    cy.get("#name").type(competition_name);
    cy.get("#competition-description").type(competition_name);
    cy.get("#image").attachFile("competition.jpg");
    cy.get("#start_date").type(start_date);
    cy.get("#end_date").type(end_date);
    cy.get(".btn-success").click();

    cy.get(".competition-card .competition-name")
        .contains(competition_name)
        .should("be.visible")
        .parents(".competition-card")
        .find(".competition-status")
        .should("have.text", "draft");
    getCompetitionIdByName(competition_name);
}

export function add_competitor(competition_name) {
    const competitor_name = randomId("Bird ");
    cy.log("Competitor name: " + competitor_name);
    cy.visit("/competitions");
    cy.get(".competition-card .competition-name")
        .contains(competition_name)
        .parents(".competition-card")
        .find(`.mange-competitors`)
        .click();

    cy.get("h1").should("have.text", competition_name);
    cy.get(".btn").contains("Add a Competitor").click();
    cy.get("form").should("be.visible");

    cy.get("#name").type(competitor_name);
    cy.get("#author").type("BirdMan");
    cy.get("#description").type("A little little bird");
    cy.get("#image").click();
    cy.get("#image").attachFile("competitor.jpg");
    cy.get(".btn-success").click();
    cy.get("h5").contains(competitor_name).should("be.visible");
}

export function edit_competition(competition_name) {
    cy.visit("/competitions");

    // Find competion card and click edit link
    cy.get(".competition-card .competition-name")
        .contains(competition_name)
        .parents(".competition-card")
        .find(`.competition-edit`)
        .click();
    cy.get("form").should("be.visible");

    // Edit competition details
    cy.get("#description")
        .invoke("text")
        .then((text) => {
            const updatedText = `${text} is coming`;
            cy.get("#description").clear().type(updatedText).should("have.value", updatedText);
        });
    cy.get("#status").select("in_plan");
    cy.get(".btn-success").click();

    // Validate competition status is updated
    cy.get(".competition-card .competition-name")
        .contains(competition_name)
        .parents(".competition-card")
        .find(`.competition-status`)
        .should("have.text", "in_plan");
}

export function view_competition(competition_name) {
    cy.visit("/");
    cy.get(".competition-card .card-title")
        .contains(competition_name)
        .parents(".competition-card")
        .find("a.stretched-link")
        .click();
    cy.get(".competition-card .card-title").should("have.text", competition_name);
    cy.get(".competitor-container img")
        .should("be.visible")
        .parents(".competitor-container")
        .find(".vote-count")
        .should("have.text", "0");
}

export function delete_competition(competition_name) {
    cy.visit("/competitions");

    // Find competion card and click delete link
    cy.get(".competition-card .competition-name")
        .contains(competition_name)
        .parents(".competition-card")
        .find(`.delete-btn`)
        .click();
    cy.get("#confirmDeleteButton").click();
    cy.get(".competition-card .competition-name").contains(competition_name).should("not.exist");
}

function getCompetitionIdByName(competition_name) {
    cy.get(".competition-card")
        .contains(competition_name)
        .parents(".competition-card")
        .invoke("attr", "data-competition-id")
        .then((competition_id) => {
            cy.log("competition_id: " + competition_id);
        });
}
