package lu.p2.steps;

import lu.p2.holders.VoteRecordHolder;
import lu.p2.models.VoteRecord;
import lu.p2.pages.DashboardPage;
import lu.p2.pages.HomePage;
import lu.p2.pages.VotingPage;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class VotingStepsTest {


    private HomePage homepage;
    private VoteRecordHolder voteRecordHolder;
    private VotingPage votingPage;
    private VotingSteps votingSteps;
    private DashboardPage dashboardPage;

    @Before
    public void setUp() {
        homepage = mock(HomePage.class);
        voteRecordHolder = mock(VoteRecordHolder.class);
        votingPage = mock(VotingPage.class);
        dashboardPage = mock(DashboardPage.class);
        votingSteps = new VotingSteps(homepage, voteRecordHolder, votingPage, dashboardPage);
    }

    @Test
    public void Can_find_an_ongoing_event() {
        final VoteRecord voteRecord = mock(VoteRecord.class);

        // Given
        given(homepage.gotoAnOngoingEvent()).willReturn(voteRecord);

        // When
        votingSteps.thereIsAnActiveEventAvailableForVoting();

        // Then
        then(homepage).should().gotoAnOngoingEvent();
        then(voteRecordHolder).should().set(voteRecord);
    }

    @Test
    public void Can_vote() {
        final VoteRecord voteRecord = mock(VoteRecord.class);
        // Given

        given(voteRecordHolder.get()).willReturn(voteRecord);

        // When
        votingSteps.iVoteTheEvent();

        // Then
        then(votingPage).should().vote(voteRecord);
    }

    @Test
    public void Can_try_to_cast_a_vote() {
        // Given

        // When
        votingSteps.iTryToCastAVote();

        // Then
        then(votingPage).should().vote();
    }

    @Test
    public void My_vote_should_be_recorded() {
        final VoteRecord voteRecord = mock(VoteRecord.class);
        // Given

        given(voteRecordHolder.get()).willReturn(voteRecord);

        // When
        votingSteps.myVoteShouldBeRecorded();

        // Then
        then(votingPage).should().checkVotes(voteRecord);
    }

    @Test
    public void Can_not_vote_again() {
        // Given

        // When
        votingSteps.iShouldNotAbleToVoteTheSameEventAgain();

        // Then
        then(votingPage).should().failToVoteAgain();
    }

    @Test
    public void Can_view_my_votes_under_dashboard() {
        final VoteRecord voteRecord = mock(VoteRecord.class);

        // Given
        given(voteRecordHolder.get()).willReturn(voteRecord);

        // When
        votingSteps.iCanSeeMyVoteUnderMyDashboard();

        // Then
        then(homepage).should().goToMyDashboardPage();
        then(dashboardPage).should().checkVotes(voteRecord);
    }
}