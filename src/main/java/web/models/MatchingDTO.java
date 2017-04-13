package web.models;


import java.util.ArrayList;
import java.util.List;

public class MatchingDTO {
    private Plan plan;
    private List<Matching> matchings;
    private List<Resource> unmatchedResources;
    private List<Member> unmatchedMembers;

    public MatchingDTO(){
        matchings = new ArrayList<>();
        unmatchedResources = new ArrayList<>();
        unmatchedMembers = new ArrayList<>();
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public List<Matching> getMatchings() {
        return matchings;
    }

    public void setMatchings(List<Matching> matchings) {
        this.matchings = matchings;
    }

    public List<Resource> getUnmatchedResources() {
        return unmatchedResources;
    }

    public void setUnmatchedResources(List<Resource> unmatchedResources) {
        this.unmatchedResources = unmatchedResources;
    }

    public List<Member> getUnmatchedMembers() {
        return unmatchedMembers;
    }

    public void setUnmatchedMembers(List<Member> unmatchedMembers) {
        this.unmatchedMembers = unmatchedMembers;
    }
}
