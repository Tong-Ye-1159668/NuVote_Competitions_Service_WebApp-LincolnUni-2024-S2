package lu.p2.models;

public class VoteRecord {

    private String candidateId;
    private String eventName;
    private String candidateName;
    private String themeName;
    private String voteDateTime;
    private String totalVotes;

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(final String candidateId) {
        this.candidateId = candidateId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(final String eventName) {
        this.eventName = eventName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(final String candidateName) {
        this.candidateName = candidateName;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(final String themeName) {
        this.themeName = themeName;
    }

    public String getVoteDateTime() {
        return voteDateTime;
    }

    public void setVoteDateTime(final String voteDateTime) {
        this.voteDateTime = voteDateTime;
    }

    public String getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(final String totalVotes) {
        this.totalVotes = totalVotes;
    }
}
