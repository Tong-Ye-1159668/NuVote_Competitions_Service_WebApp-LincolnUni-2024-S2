package lu.p2.pages;

import lu.p2.io.DateTimeComparison;
import lu.p2.io.Helper;
import lu.p2.models.VoteRecord;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Component
public class VotingPage {
    private final Finders finders;
    private final Helper helper;
    private final DateTimeComparison dateTimeComparison;

    public VotingPage(final Finders finders, final Helper helper, final DateTimeComparison dateTimeComparison) {
        this.finders = finders;
        this.helper = helper;
        this.dateTimeComparison = dateTimeComparison;
    }

    public VoteRecord vote(VoteRecord voteRecord) {
        finders.waitByClassName("candidate-container");
        final WebElement selectedElement = helper.getRandomElement(finders.findAllByClassName("candidate-card"));
        final String candidateName = selectedElement.findElement(Bys.tagName("img")).getAttribute("alt");
        voteRecord.setCandidateName(candidateName);
        final String count = String.valueOf(Integer.parseInt(selectedElement.findElement(Bys.className("vote-count")).getText()) + 1);
        final WebElement voteWE = selectedElement.findElement(Bys.className("candidate-vote-container")).findElement(Bys.text("a", "Vote"));
        voteRecord.setCandidateId(voteWE.getAttribute("data-id"));
        voteWE.click();
        finders.waitByClassName("modal-dialog");
        finders.clickById("confirmVoteBtn");
        voteRecord.setTotalVotes(count);
        voteRecord.setVoteDateTime(dateTimeComparison.getCurrentDateTime());
        return voteRecord;
    }

    public void vote() {
        finders.waitByClassName("candidate-container");
        final WebElement selectedElement = helper.getRandomElement(finders.findAllByClassName("candidate-card"));
        selectedElement.findElement(Bys.className("candidate-vote-container")).findElement(Bys.text("a", "Vote")).click();
    }

    public void checkVotes(final VoteRecord voteRecord) {
        finders.waitByText("div", "You have voted successfully!");
        final String actualCount = finders.findByAttribute("div", "data-candidate-id", voteRecord.getCandidateId()).findElement(Bys.className("vote-count")).getText();
        assertThat(TRUE, is(actualCount.equals(voteRecord.getTotalVotes())));
    }

    public void failToVoteAgain() {
        finders.waitByClassName("alert");
        finders.findByText("span", "Voted");
//        finders.findAllByText("a", "Vote");
    }
}
