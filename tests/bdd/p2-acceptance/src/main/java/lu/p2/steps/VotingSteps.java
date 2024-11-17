package lu.p2.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lu.p2.holders.VoteRecordHolder;
import lu.p2.models.VoteRecord;
import lu.p2.pages.DashboardPage;
import lu.p2.pages.HomePage;
import lu.p2.pages.VotingPage;

public class VotingSteps {
    private final HomePage homepage;
    private final VoteRecordHolder voteRecordHolder;
    private final VotingPage votingPage;
    private final DashboardPage dashboardPage;

    public VotingSteps(final HomePage homepage, final VoteRecordHolder voteRecordHolder, final VotingPage votingPage, final DashboardPage dashboardPage) {
        this.homepage = homepage;
        this.voteRecordHolder = voteRecordHolder;
        this.votingPage = votingPage;
        this.dashboardPage = dashboardPage;
    }

    @And("there is an active event available for voting")
    public void thereIsAnActiveEventAvailableForVoting() {
        voteRecordHolder.set(homepage.gotoAnOngoingEvent());
    }

    @When("I vote the event")
    public void iVoteTheEvent() {
        final VoteRecord voteRecord = votingPage.vote(voteRecordHolder.get());
        voteRecordHolder.set(voteRecord);
    }

    @When("I try to cast a vote")
    public void iTryToCastAVote() {
        votingPage.vote();
    }

    @Then("my vote should be recorded")
    public void myVoteShouldBeRecorded() {
        votingPage.checkVotes(voteRecordHolder.get());
    }

    @And("I should not able to vote the same event again")
    public void iShouldNotAbleToVoteTheSameEventAgain() {
        votingPage.failToVoteAgain();
    }

    @And("I can see my vote under my dashboard")
    public void iCanSeeMyVoteUnderMyDashboard() {
        homepage.goToMyDashboardPage();
        dashboardPage.checkVotes(voteRecordHolder.get());
    }
}
