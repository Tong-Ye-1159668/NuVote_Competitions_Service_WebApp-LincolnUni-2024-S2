import {ADMIN_USERNAME, COMPETITION_ID_FINISHED, DEFAULT_PASSWORD, SCRUTINEER_USERNAME} from "../constants";
import { create_user, login, logout } from "../components/user";
import { disable_vote } from "../components/vote";

const username = SCRUTINEER_USERNAME;

describe("Scrutineer", () => {
    beforeEach(() => {
        
    });
    afterEach(() => {
        // logout();  // This is causing the test to fail
    });
    it("Create a scrutineer", () => {
        cy.log("Username: " + username);
        login(ADMIN_USERNAME, DEFAULT_PASSWORD);
        create_user(username, "scrutineer");
    });
    it("Disable a vote", () => {
        login(username, DEFAULT_PASSWORD);
        disable_vote(COMPETITION_ID_FINISHED);
    });
});