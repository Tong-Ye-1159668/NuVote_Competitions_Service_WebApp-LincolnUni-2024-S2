import { login, logout } from "../components/user";
import { ADMIN_USERNAME, COMPETITION_NAME, DEFAULT_PASSWORD } from "../constants";
import { randomId } from "../utils";
import {
    create_competition,
    add_competitor,
    edit_competition,
    view_competition,
    delete_competition,
} from "../components/competition";

const competition_name = COMPETITION_NAME

describe("Admin", () => {
    // before each login and after each logout
    beforeEach(() => {
        login(ADMIN_USERNAME, DEFAULT_PASSWORD);
    });
    afterEach(() => {
        logout();
    });
    it("Create a new competition", () => {
        cy.log("Competition name: " + competition_name);
        create_competition(competition_name);
    });
    it("Add a competitor", () => {
        add_competitor(competition_name);
    });
    it("Edit a competition", () => {
        edit_competition(competition_name);
    });
    it("View vote page", () => {
        view_competition(competition_name);
    });
    it("Delete competition", () => {
        delete_competition(competition_name);
    });
});
