package lu.p2.pages;

import lu.p2.io.DateTimeComparison;
import lu.p2.io.Helper;
import lu.p2.models.VoteRecord;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomIntegers.somePositiveInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class VotingPageTest {

    private Finders finders;
    private Helper helper;
    private DateTimeComparison dateTimeComparison;
    private VotingPage votingPage;

    @Before
    public void setUp() {
        finders = mock(Finders.class);
        helper = mock(Helper.class);
        dateTimeComparison = mock(DateTimeComparison.class);
        votingPage = new VotingPage(finders, helper, dateTimeComparison);
    }

    @Test
    public void Can_vote_a_candidate() {
        final VoteRecord voteRecord = mock(VoteRecord.class);
        final WebElement candidateWE = mock(WebElement.class, RETURNS_DEEP_STUBS);
        final List<WebElement> list = List.of(mock(WebElement.class), candidateWE, mock(WebElement.class));
        final String candidateName = someString();
        final int count = somePositiveInteger();
        final WebElement voteWE = mock(WebElement.class);
        final String candidateId = someString();
        final String currentDatetime = someString();

        // Given
        given(finders.findAllByClassName("candidate-card")).willReturn(list);
        given(helper.getRandomElement(list)).willReturn(candidateWE);
        given(candidateWE.findElement(Bys.tagName("img")).getAttribute("alt")).willReturn(candidateName);
        given(candidateWE.findElement(Bys.className("vote-count")).getText()).willReturn(String.valueOf(count));
        given(candidateWE.findElement(Bys.className("candidate-vote-container")).findElement(Bys.text("a", "Vote"))).willReturn(voteWE);
        given(voteWE.getAttribute("data-id")).willReturn(candidateId);
        given(dateTimeComparison.getCurrentDateTime()).willReturn(currentDatetime);

        // When
        votingPage.vote(voteRecord);

        // Then
        then(voteRecord).should().setCandidateName(candidateName);
        then(voteRecord).should().setCandidateId(candidateId);
        then(voteWE).should().click();
        then(finders).should().clickById("confirmVoteBtn");
        then(voteRecord).should().setTotalVotes(String.valueOf(count + 1));
        then(voteRecord).should().setVoteDateTime(currentDatetime);
    }

    @Test
    public void Can_vote() {
        final WebElement candidateWE = mock(WebElement.class, RETURNS_DEEP_STUBS);
        final List<WebElement> list = List.of(mock(WebElement.class), candidateWE, mock(WebElement.class));
        final WebElement voteWE = mock(WebElement.class);

        // Given
        given(finders.findAllByClassName("candidate-card")).willReturn(list);
        given(helper.getRandomElement(list)).willReturn(candidateWE);
        given(candidateWE.findElement(Bys.className("candidate-vote-container")).findElement(Bys.text("a", "Vote"))).willReturn(voteWE);

        // When
        votingPage.vote();

        // Then

        then(voteWE).should().click();
    }

    @Test
    public void Can_check_vote() {
        final VoteRecord voteRecord = mock(VoteRecord.class);
        final String id = someString();
        final String totalCount = someString();
        final WebElement webElement = mock(WebElement.class, RETURNS_DEEP_STUBS);

        // Given
        given(voteRecord.getCandidateId()).willReturn(id);
        given(voteRecord.getTotalVotes()).willReturn(totalCount);
        given(finders.findByAttribute("div", "data-candidate-id", id)).willReturn(webElement);
        given(webElement.findElement(Bys.className("vote-count")).getText()).willReturn(totalCount);

        // When
        votingPage.checkVotes(voteRecord);

        // Then
    }

    @Test
    public void Can_fail_to_vote() {
        // Given
        given(finders.findByText("span", "Voted")).willReturn(mock(WebElement.class));
//        given(finders.findAllByText("a", "Vote")).willReturn(List.of());

        // When
        votingPage.failToVoteAgain();

        // Then
    }
}