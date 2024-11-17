import { register, logout, login } from "../components/user";
import { VOTER_USERNAME, COMPETITION_ID_TO_VOTE, DEFAULT_PASSWORD, COMPETITION_ID_APPROVED } from "../constants";
import { cast_a_vote, view_vote_result } from "../components/vote";

const username = VOTER_USERNAME;

describe("Voter", () => {
    afterEach(() => {
        logout();
    });
    it("Register new user", () => {
        cy.log("Registering new user: " + username);
        register(username);
        cy.wait(1000);
    });
    it("Login as voter", () => {
        login(username, DEFAULT_PASSWORD);
    });
    it("Cast a vote", () => {
        login(username, DEFAULT_PASSWORD);
        cast_a_vote(COMPETITION_ID_TO_VOTE);
    });
    it("View vote result", () => {
        login(username, DEFAULT_PASSWORD);
        view_vote_result(COMPETITION_ID_APPROVED);
    });
});
