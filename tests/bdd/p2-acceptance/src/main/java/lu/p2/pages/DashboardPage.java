package lu.p2.pages;

import lu.p2.io.DateTimeComparison;
import lu.p2.models.Location;
import lu.p2.models.User;
import lu.p2.models.VoteRecord;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.springframework.stereotype.Component;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Component
public class DashboardPage {

    private final Finders finders;
    private final DateTimeComparison dateTimeComparison;

    public DashboardPage(final Finders finders, final DateTimeComparison dateTimeComparison) {
        this.finders = finders;
        this.dateTimeComparison = dateTimeComparison;
    }

    public boolean verifyMyDetails(final User user) {
        finders.findByText("h2", user.getUsername());
        if (user.getFirstName() != null) {
            finders.findByText("div", user.getFirstName());
        }
        if (user.getLastName() != null) {
            finders.findByText("div", user.getLastName());
        }
        if (user.getDescription() != null) {
            finders.findByText("div", user.getDescription());
        }
        return true;
    }

    public void clickEditBtn() {
        finders.clickByText("a", "Edit");
    }

    public boolean isDefaultProfileImage() {
        finders.waitById("profile-image");
        return finders.findById("profile-image").getAttribute("src").contains("static/images/default_profile.png");
    }

    public boolean checkMyRole(final String role) {
        return finders.findByText("strong", "Role:").findElement(Bys.parentClassName("div", "list-group-item")).getText().contains(role);
    }

    public void checkVotes(final VoteRecord voteRecord) {
        finders.waitById("votes-tab");
        finders.clickById("votes-tab");
        finders.waitById("recentVotesBody");
        finders.findByText("a", voteRecord.getEventName());
        finders.findByText("td", voteRecord.getCandidateName());
        finders.findByText("td", voteRecord.getThemeName());
        final String datetimeString = finders.findById("recentVotesBody").findElement(Bys.tagName("tr")).findElement(Bys.tagName("td")).getText();
        assertThat(TRUE, is(dateTimeComparison.isLessThanSomeMinutes(datetimeString, 3)));
    }

    public void checkMyLocation(final Location location) {
        finders.waitById("dashboardTabsContent");
        finders.waitById("map");
        finders.findByText("b", location.getAddress());
        finders.findByAttribute("input", "value", extractAndRound(location.getLatitude(), 8));
        finders.findByAttribute("input", "value", extractAndRound(location.getLongitude(), 8));
    }

    private String extractAndRound(final String numberStr, final int decimalPlaces) {
        double number = Double.parseDouble(numberStr);
        String format = "%." + decimalPlaces + "f";
        return String.format(format, number);
    }
}
